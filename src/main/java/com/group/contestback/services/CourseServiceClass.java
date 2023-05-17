package com.group.contestback.services;

import com.group.contestback.models.*;
import com.group.contestback.repositories.*;
import com.group.contestback.responseTypes.GroupCoursesWithNames;
import com.group.contestback.responseTypes.StudentTaskResponse;
import com.group.contestback.responseTypes.TaskResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CourseServiceClass implements CourseService{
    private final CoursesRepo coursesRepo;
    private final TaskCoursesRepo taskCoursesRepo;
    private final GroupCoursesRepo groupCoursesRepo;
    private final AppUserRepo appUserRepo;
    private final TaskDeadlinesRepo taskDeadlinesRepo;
    private final AttemptsRepo attemptsRepo;
    private final LecturesRepo lecturesRepo;

    @Override
    public void addCourse(String name, Integer year, Integer teacherId) {
        Courses courses = new Courses(name, year, teacherId);
        coursesRepo.save(courses);
    }

    @Override
    public void updateCourse(Courses course) {
        Courses courseOld = coursesRepo.getById(course.getId());
        courseOld.setName(course.getName());
        courseOld.setYear(course.getYear());
        courseOld.setTeacherId(course.getTeacherId());
        coursesRepo.save(courseOld);
    }

    @Override
    public List<Courses> getCoursesByTeacher(Integer teacherId) {
        return coursesRepo.findByTeacherId(teacherId);
    }

    @Override
    public List<Courses> getAllCourses() {
        return coursesRepo.findAll();
    }

//    @Override
//    public List<Courses> getAllCoursesNew() {
//        return coursesRepo.findAllCourseAndDeletedIsFalse();
//    }
//
//    @Override
//    public List<Courses> getCoursesToTeacher(Integer teacherId) {
//        return coursesRepo.findByTeacherId(teacherId);
//    }

    @Override
    public List<StudentTaskResponse> getStudentCourses() {
        AppUser appUser = appUserRepo.findByLogin(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        List<GroupCourses> groupCourses
                = groupCoursesRepo.findAllByGroupId(appUser.getGroupId());
        List<Courses> courses = new ArrayList<>();

        List<StudentTaskResponse> resp = new ArrayList<>();

        for (int i = 0; i < groupCourses.size(); ++i) {
            StudentTaskResponse studentTaskResponse = new StudentTaskResponse();

            Courses courses1 = coursesRepo.findById(groupCourses.get(i).getCourseId()).get();
            studentTaskResponse.setUserId(appUser.getId());
            studentTaskResponse.setCourses(courses1);
            List<Attempts> userAttempts = attemptsRepo.findAllByCourseIdAndUserId(courses1.getId(), appUser.getId());
            List<TaskCourses> taskCourses = taskCoursesRepo.findAllByCourseId(courses1.getId());
            Set<Integer> set = new HashSet<Integer> ();

            for(int k = 0; k < userAttempts.size(); ++k) {
                set.add(userAttempts.get(k).getTaskId());
            }
            studentTaskResponse.setCompletion(set.size()/taskCourses.size());

            List<TaskDeadlines> taskDeadlines = taskDeadlinesRepo.findAllByCourseId(courses1.getId());
            Comparator<TaskDeadlines> comparator = (p1, p2) -> (int) (p2.getDeadline().getTime() - p1.getDeadline().getTime());
            taskDeadlines.sort(comparator);
            if(taskDeadlines.size() > 0) {
                studentTaskResponse.setNearestDeadline(taskDeadlines.get(0).getDeadline());
            }
            resp.add(studentTaskResponse);
        }
        return resp;
    }

    @Override
    public void deleteCourseById(Integer courseId) {
        taskCoursesRepo.deleteAllByCourseId(courseId);
        lecturesRepo.deleteAllByCourseId(courseId);

        coursesRepo.deleteById(courseId);
    }

//    @Override
//    public void removeCourse(Integer courseId) {
//        Courses courses = coursesRepo.getById(courseId);
//        courses.setDeleted(true);
//        coursesRepo.save(courses);
//    }
}
