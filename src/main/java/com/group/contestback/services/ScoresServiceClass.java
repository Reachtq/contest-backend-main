package com.group.contestback.services;


import com.group.contestback.common.SendEmailNotification;
import com.group.contestback.models.*;
import com.group.contestback.repositories.*;
import com.group.contestback.responseTypes.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ScoresServiceClass implements ScoresService {
    private final ScoresRepo scoresRepo;
    private final AttemptsRepo attemptsRepo;
    private final AppUserRepo appUserRepo;
    private final RolesRepo rolesRepo;
    private final TasksRepo tasksRepo;
    private final TaskTypesRepo taskTypesRepo;
    private final TaskCoursesRepo taskCoursesRepo;
    private final GroupsRepo groupsRepo;
    private final SolutionVariantsRepo solutionVariantsRepo;
    private final TaskDeadlinesRepo taskDeadlinesRepo;
    private final CoursesRepo coursesRepo;
    private final RestTemplate restTemplate;
    private final SendEmailNotification sendNotification = new SendEmailNotification();
    @Value("${config.sql-tests.server.url}")
    private String serverUrl;

    @Override
    public void addScore(Integer userId, Integer taskId, Integer courseId, Integer score, String review) {

        List<Attempts> attempts = attemptsRepo.findAllByTaskIdAndUserId(taskId, userId);
        Comparator<Attempts> comparator = (p1, p2) -> (int) (p2.getTime().getTime() - p1.getTime().getTime());
        attempts.sort(comparator);
        AppUser appUser = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        Scores scores = new Scores(userId, taskId, score, appUser.getId(), review, courseId);
        scores.setSolution(attempts.get(0).getSolution());

        scoresRepo.save(scores);
        String email = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getEmail();
        sendNotification.sendGradeNotification(appUser.getFio(), email, scores.getScore(), review);
    }

    @Override
    public void updateScore(Scores score, String review) {

        List<Attempts> attempts = attemptsRepo.findAllByTaskIdAndUserId(score.getTaskId(), score.getUserId());
        Comparator<Attempts> comparator = (p1, p2) -> (int) (p2.getTime().getTime() - p1.getTime().getTime());
        attempts.sort(comparator);
        AppUser appUser = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        Scores scoresOld = scoresRepo.getById(score.getId());
        scoresOld.setScore(score.getScore());
        scoresOld.setTeacherId(score.getTeacherId());

        scoresRepo.save(scoresOld);
        String email = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getEmail();
        sendNotification.sendGradeNotification(appUser.getFio(), email, score.getScore(), review);
    }


    @Override
    public List<Scores> getAllScores() {
        return scoresRepo.findAll();
    }

    @Override
    public void addAttempt(Attempts attempt) {
        attemptsRepo.save(attempt);
    }

    @Override
    public List<Attempts> getAllAttempts() {
        return attemptsRepo.findAll();
    }


    @Override
    public String checkSQLSolution(Integer taskId, Integer courseId, String solution) {
        Integer userId = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getId();
        String teacherSolution = tasksRepo.getById(taskId).getSolution();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(serverUrl + "/checkAnswer");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String json = "{" +
                "    \"studentAnswer\": \"" + solution + "\"," +
                "    \"teacherAnswer\": \"" + teacherSolution + "\"" +
                "}";

        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            InputStream inputStream = responseEntity.getContent(); // получение входного потока содержимого ответа
            // чтение содержимого ответа
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String responseBody = scanner.hasNext() ? scanner.next() : "";
            inputStream.close();
            Attempts attempts = new Attempts(userId, taskId, true, solution, courseId);
            if (responseBody.equals("false")) {
                attempts.setSucceeded(false);
            }
            attemptsRepo.save(attempts);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Server not available";
    }

    @Override
    public String checkSQLSolutionScore(Integer taskId, Integer courseId, String solution) {
        Integer userId = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getId();
        String teacherSolution = tasksRepo.getById(taskId).getSolution();
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost request = new HttpPost(serverUrl + "/checkAnswerOnMark");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        String json = "{" +
                "    \"studentAnswer\": \"" + solution + "\"," +
                "    \"teacherAnswer\": \"" + teacherSolution + "\"" +
                "}";

        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        request.setEntity(entity);

        try {
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity responseEntity = response.getEntity();
            InputStream inputStream = responseEntity.getContent(); // получение входного потока содержимого ответа
            // чтение содержимого ответа
            Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
            String responseBody = scanner.hasNext() ? scanner.next() : "";
            inputStream.close();
            Attempts attempts = new Attempts(userId, taskId, true, solution, courseId);
            if (responseBody.equals("true")) {
                Scores score = new Scores(userId, taskId, 5, null, courseId, solution);
                scoresRepo.save(score);
            } else {
                attempts.setSucceeded(false);
            }
            attemptsRepo.save(attempts);
            return responseBody;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Server not available";
    }

    @Override
    public Integer checkSimpleSolution(Integer taskId, Integer courseId, List<Integer> solutionsId) {
        Integer userId = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getId();
        Tasks task = tasksRepo.findById(taskId).get();
        List<Attempts> attempts = attemptsRepo.findAllByTaskIdAndUserId(taskId, userId);
        String taskType = taskTypesRepo.getById(task.getTaskTypeId()).getName();
        List<TaskCourses> taskCourses = taskCoursesRepo.findAllByCourseId(courseId);
        if (!(taskCourses.size() > 0 && taskCourses.stream().anyMatch(t -> {
            return t.getTaskId().equals(taskId);
        }))) {
            throw new RuntimeException("This task is not on this course");
        }
        if (!taskType.equals("SIMPLE_TASK")) {
            throw new RuntimeException("Wrong request for task type");
        }
        if (attempts.size() > 0) {
            throw new RuntimeException("Only 1 attempt allowed");
        }
        List<TaskDeadlines> tdl = taskDeadlinesRepo.findAllByTaskIdAndCourseId(task.getId(), courseId);

        if (tdl.size() > 0 && (tdl.get(0).getDeadline().getTime() - new Date().getTime() < 0)) {
            throw new RuntimeException("The deadline expired");
        }


        // SIMPLE_TASK - only 1 attempt
        List<SolutionVariants> solutionVariants = solutionVariantsRepo.findAllByTaskId(taskId);
        List<Integer> rightSolutions = new ArrayList<>();

        for (SolutionVariants solutionVariant : solutionVariants) {
            if (solutionVariant.getIsAnswer()) {
                rightSolutions.add(solutionVariant.getId());
            }
        }
        if (rightSolutions.size() == 0) {
            throw new RuntimeException("Task doesn't have right solutions, contact administrator");
        }
        Integer numberOfWrongSolutions = 0;
        Integer numberOfRightSolutions = 0;

        for (Integer solutionId : solutionsId) {
            if (!rightSolutions.contains(solutionId)) {
                numberOfWrongSolutions++;
            } else {
                numberOfRightSolutions++;
            }
        }
        Integer result = numberOfRightSolutions / rightSolutions.size() * 4 - numberOfWrongSolutions * 2 + 1;
        if (result < 1) {
            result = 1;
        }
        Attempts attempt = new Attempts(userId, taskId, result == 5, solutionsId.toString(), courseId);
        attemptsRepo.save(attempt);
        Scores score = new Scores(userId, taskId, result, null, courseId, solutionsId.toString());
        scoresRepo.save(score);
        return result;
    }

    @Override
    public List<ScoresResponse> getStudentScores() {
        List<ScoresResponse> response = new ArrayList<>();
        List<Scores> allScores = scoresRepo.findAllByUserId(appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getId());
        List<Scores> lastScores = new ArrayList<>();
        for (Scores scores : allScores) {
            Integer taskId = scores.getTaskId();
            Date date = scores.getDate();
            for (int k = 0; k < lastScores.size(); ++k) {
                Scores lastScore = lastScores.get(k);
                if (lastScore.getTaskId().equals(taskId) && ((lastScore.getDate().getTime() - date.getTime()) < 0)) {
                    lastScores.remove(lastScore);
                }
            }
            lastScores.add(scores);
        }
        for (Scores score : lastScores) {
            ScoresResponse sr = new ScoresResponse();
            sr.setScore(score);
            Tasks task = tasksRepo.findById(score.getTaskId()).get();
            sr.setTask(new Tasks(task.getId(), task.getName(), task.getDescription(), "", task.getTaskTypeId()));
            sr.setCourse(coursesRepo.findById(score.getCourseId()).get());
            if (score.getTeacherId() != null) {
                AppUser teacher = appUserRepo.findById(score.getTeacherId()).get();
                sr.setTeacherName(teacher.getFirstName());
                sr.setTeacherLastName(teacher.getLastName());
                sr.setTeacherMiddleName(teacher.getMiddleName());
            }

            response.add(sr);
        }
        return response;
    }

    @Override
    public Integer getStudentScore(Integer userId, Integer taskId) {
        List<Scores> allScores = scoresRepo.findAllByUserId(userId);
        List<Scores> lastScores = new ArrayList<>();
        for (Scores scores : allScores) {
            if (Objects.equals(taskId, scores.getTaskId())) {
                Date date = scores.getDate();
                for (int k = 0; k < lastScores.size(); ++k) {
                    Scores lastScore = lastScores.get(k);
                    if (lastScore.getTaskId().equals(taskId) && ((lastScore.getDate().getTime() - date.getTime()) < 0)) {
                        lastScores.remove(lastScore);
                    }
                }
                lastScores.add(scores);
            }
        }
        if (!lastScores.isEmpty()) {
            return lastScores.get(0).getScore();
        }
        return 0;
    }

    @Override
    public List<ScoresUser> getGroupScoresForTask(Integer groupId, Integer taskId) {
        List<ScoresUser> response = new ArrayList<>();
        List<Scores> scores = scoresRepo.findAllByTaskId(taskId);

        Comparator<Scores> comparator = (p1, p2) -> (int) (p2.getDate().getTime() - p1.getDate().getTime());
        scores.sort(comparator);
        List<AppUser> users = appUserRepo.findAllByGroupId(groupId);
        List<Roles> roleNameToId = rolesRepo.findAll();
        List<Groups> groupNameToId = groupsRepo.findAll();

        users.forEach(appUser -> {
            if (rolesRepo.getById(appUser.getRoleId()).getName().equals("ROLE_USER")) {
                ScoresUser su = new ScoresUser();
                su.setUser(new UserPageResponse(appUser.getId(), appUser.getFirstName(), appUser.getLastName()
                        , appUser.getMiddleName(), appUser.getLogin(), appUser.getEmail(), appUser.getRoleId(), appUser.getGroupId(),
                        roleNameToId.stream().filter(role -> role.getId().equals(appUser.getRoleId()))
                                .findAny()
                                .orElse(new Roles()).getName(),
                        groupNameToId.stream().filter(gr -> gr.getId().equals(appUser.getGroupId()))
                                .findAny().orElse(new Groups()).getNumber()));
                scores.forEach(scores1 -> {
                    if (appUser.getId().equals(scores1.getUserId()) && !su.getScores().stream().anyMatch(s ->
                            (s.getUserId().equals(scores1.getUserId()) && s.getCourseId().equals(scores1.getCourseId())))) {
                        su.getScores().add(scores1);
                    }
                });
                if (su.getScores().size() == 0 && rolesRepo.getById(appUser.getRoleId()).getName().equals("ROLE_USER")) {
                    su.getScores().add(new Scores(null, appUser.getId(), taskId, null, null, null, null, null, null));
                }
                response.add(su);
            }
        });
        return response;
    }

    @Override
    public List<Attempts> getStudentAttemptsOnTask(Integer taskId) {
        return attemptsRepo.findAllByTaskIdAndUserId(taskId, appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString()).getId());
    }

    @Override
    public GroupCoursesScoresResponse getGroupScoresForCourse(Integer groupId, Integer courseId) {
        List<TaskCourses> taskCourses = taskCoursesRepo.findAllByCourseId(courseId);
        List<AppUser> users = appUserRepo.findAllByGroupId(groupId);
        List<Attempts> attempts = attemptsRepo.findAllByCourseId(courseId);
        GroupCoursesScoresResponse groupCoursesScoresResponse = new GroupCoursesScoresResponse();

        for (AppUser user : users) {
            List<Scores> userScores = new ArrayList<>();
            for (TaskCourses taskCourse : taskCourses) {
                List<Attempts> userAttempts = attempts.stream().filter(a -> a.getUserId().equals(user.getId())
                        && a.getTaskId().equals(taskCourse.getTaskId()) && a.getCourseId().equals(taskCourse.getCourseId())).toList();
                Attempts lastAttempt = userAttempts.stream().max(Comparator.comparing(v -> v.getTime().getTime())).orElse(new Attempts());

                List<Scores> scores = scoresRepo.findAllByUserIdAndTaskId(user.getId(), taskCourse.getTaskId());
                if (scores.size() > 0) {
                    Comparator<Scores> comparator = (p1, p2) -> (int) (p2.getDate().getTime() - p1.getDate().getTime());
                    scores.sort(comparator);
                    log.info("adding");
                    log.info(String.valueOf(scores.get(0).getUserId()));
                    log.info(String.valueOf(scores.get(0).getTaskId()));

                    userScores.add(scores.get(0));
                } else {
                    Scores nullScore = new Scores(user.getId(), taskCourse.getTaskId(), (Integer) null, (Integer) null, courseId, lastAttempt.getSolution());
                    userScores.add(nullScore);
                }
            }
            groupCoursesScoresResponse.addUser(userScores, user.getId(), user.getLogin(), user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getEmail(), user.getRoleId(), user.getGroupId());
        }

        return groupCoursesScoresResponse;
    }

    @Override
    public List<GroupStudents> getAllManualAttempts(String courseId) {
        List<GroupStudents> groupStudents = new ArrayList<>();
        List<Groups> groups = groupsRepo.findAll();
        List<Tasks> manualTasks = tasksRepo.findAllByTaskTypeId(3);
        List<Integer> manualTasksIds = new ArrayList<>();
        List<Roles> roleNameToId = rolesRepo.findAll();
        List<Groups> groupNameToId = groupsRepo.findAll();
        for (Tasks task : manualTasks) {
            manualTasksIds.add(task.getId());
        }

        for (Groups group : groups) {
            GroupStudents groupStud = new GroupStudents();
            groupStud.setGroups(group);
            List<AppUser> users = appUserRepo.findAllByGroupId(group.getId());
            for (AppUser user : users) {
                List<Attempts> attempts = attemptsRepo.findByTaskUserMaxTime(user.getId(), Integer.parseInt(courseId));
                List<Attempts> manualLastAttempts = new ArrayList<>();
                List<AttemptsTask> attemptsTasks = new ArrayList<>();

                for (Attempts attempt : attempts) {
                    if (manualTasksIds.contains(attempt.getTaskId())) {
                        manualLastAttempts.add(attempt);
                        List<Tasks> allTasks = manualTasks.stream().filter(f -> f.getId().equals(attempt.getTaskId())).toList();
                        Tasks task = new Tasks();
                        if (allTasks.size() > 0) {
                            task = allTasks.get(0);
                        }
                        attemptsTasks.add(new AttemptsTask(attempt, task));
                    }
                }

                groupStud.addUser(new UserPageResponse(user.getId(), user.getFirstName(), user.getLastName(),
                        user.getMiddleName(), user.getLogin(), user.getEmail(), user.getRoleId(), user.getGroupId(),
                        roleNameToId.stream().filter(role -> role.getId().equals(user.getRoleId()))
                                .findAny()
                                .orElse(new Roles()).getName(),
                        groupNameToId.stream().filter(gr -> gr.getId().equals(user.getGroupId()))
                                .findAny().orElse(new Groups()).getNumber()), attemptsTasks);
            }
            groupStudents.add(groupStud);
        }
        return groupStudents;
    }

//    @Override
//    public void removeScore(Integer scoreId) {
//        Scores scores = scoresRepo.getById(scoreId);
//        scores.setDeleted(true);
//        scoresRepo.save(scores);
//    }
    @Override
    public void deleteScoreById(Integer id) {
    scoresRepo.deleteById(id);
}
}



