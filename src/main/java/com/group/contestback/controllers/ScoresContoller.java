package com.group.contestback.controllers;

import com.group.contestback.common.GenerateReport;
import com.group.contestback.common.GenerateUsersPasswordAndLoginXLSX;
import com.group.contestback.repositories.AppUserRepo;
import com.group.contestback.repositories.GroupsRepo;
import com.group.contestback.repositories.TaskCoursesRepo;
import com.group.contestback.services.ScoresService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

@Api(tags = {"Scores controller"})
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/score")
public class ScoresContoller {
    private final GroupsRepo groupsRepo;
    private final AppUserRepo appUserRepo;
    private final TaskCoursesRepo taskCoursesRepo;
    private final ScoresService scoresService;

    @ApiOperation(value = "Добавляет новую оценку")
    @PostMapping("/addScore")
    public ResponseEntity<?> addScore(@RequestBody ScoreForm scoreForm) {
        scoresService.addScore(scoreForm.getUserId(),scoreForm.getTaskId(),
                scoreForm.getCourseId(),scoreForm.getScore(), scoreForm.getReview());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Возращает все оценки")
    @GetMapping("/allScores")
    public ResponseEntity<?> getAllScores() {
        return ResponseEntity.ok().body(scoresService.getAllScores());
    }

    @ApiOperation(value = "Возращает оценки студента")
    @GetMapping("/studentScores")
    public ResponseEntity<?> getStudentScores() {
        return ResponseEntity.ok().body(scoresService.getStudentScores());
    }

    @ApiOperation(value = "Возращает попытки студента к заданию")
    @GetMapping("/studentAttempts/{taskId}")
    public ResponseEntity<?> getStudentAttempts(@PathVariable String taskId) {
        return ResponseEntity.ok().body(scoresService.getStudentAttemptsOnTask(Integer.parseInt(taskId)));
    }

    @ApiOperation(value = "Возращает по группам непроверяемые попытки")
    @GetMapping("/allManualAttempts/{courseId}")
    public ResponseEntity<?> getAllManualAttempts(@PathVariable String courseId) {
        return ResponseEntity.ok().body(scoresService.getAllManualAttempts(courseId));
    }

    @ApiOperation(value = "Возращает оценки группы по заданию")
    @GetMapping("/groupScoresForTask/{groupId}/{taskId}")
    public ResponseEntity<?> getGroupScoresForTask(@PathVariable String groupId, @PathVariable String taskId) {
        return ResponseEntity.ok().body(scoresService.getGroupScoresForTask(Integer.parseInt(groupId), Integer.parseInt(taskId)));
    }
    @ApiOperation(value = "Возращает оценки группы по курсу")
    @GetMapping("/groupScoresForCourse/{groupId}/{courseId}")
    public ResponseEntity<?> getGroupScoresForCourse(@PathVariable String groupId, @PathVariable String courseId) {
        return ResponseEntity.ok().body(scoresService.getGroupScoresForCourse(Integer.parseInt(courseId), Integer.parseInt(groupId)));
    }

    @ApiOperation(value = "Возращает оценки группы по курсу в файле")
    @GetMapping("/generateReport/{groupId}/{courseId}")
    public ResponseEntity<?> getGroupReportForCourse(@PathVariable String groupId, @PathVariable String courseId,
                                                     HttpServletResponse response) throws IOException {
        GenerateReport generateReport = new GenerateReport(appUserRepo, taskCoursesRepo, groupsRepo, scoresService);

        XSSFWorkbook workbook = generateReport.createReport(response, Integer.parseInt(groupId), Integer.parseInt(courseId));

        OutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.flush();
        outputStream.close();
        workbook.close();

        return ResponseEntity.ok().build();

    }
    @ApiOperation(value = "Добавляет новую попытку на оценку")
    @PostMapping("/addSQLAttemptScore")
    public ResponseEntity<?> addSQLAttemptScore(@RequestBody AttemptSQLForm attemptForm) throws ParseException {
        return ResponseEntity.ok().body(scoresService.checkSQLSolutionScore(attemptForm.getTaskId(), attemptForm.getCourseId(), attemptForm.getSolution()));
    }

    @ApiOperation(value = "Добавляет новую попытку")
    @PostMapping("/addSQLAttempt")
    public ResponseEntity<?> addSQLAttempt(@RequestBody AttemptSQLForm attemptForm) throws ParseException {
        return ResponseEntity.ok().body(scoresService.checkSQLSolution(attemptForm.getTaskId(), attemptForm.getCourseId(), attemptForm.getSolution()));
    }
    @ApiOperation(value = "Добавляет новую попытку простого задания")
    @PostMapping("/addSimpleAttempt")
    public ResponseEntity<?> addSimpleAttempt(@RequestBody AttemptSimpleForm attemptForm) {
        return ResponseEntity.ok().body(scoresService.checkSimpleSolution(attemptForm.getTaskId(), attemptForm.getCourseId(),attemptForm.getSolutionsId()));
    }

    @ApiOperation(value = "Возращает все попытки")
    @GetMapping("/allAttempts")
    public ResponseEntity<?> getAllAttempts() {
        return ResponseEntity.ok().body(scoresService.getAllAttempts());
    }

    @ApiOperation(value = "Удаление оценки")
    @DeleteMapping("/deleteScore/{scoreId}")
    public ResponseEntity<?> deleteScore(@PathVariable String scoreId) {
        scoresService.deleteScoreById(Integer.parseInt(scoreId));
        return ResponseEntity.ok().build();
    }
}
@Data
class ScoreForm {
    private Integer userId;
    private Integer taskId;
    private Integer score;
    private String review;
    private Integer courseId;
}
@Data
class AttemptSQLForm {
    private Integer taskId;
    private Integer courseId;
    private String solution;
}
@Data
class AttemptSimpleForm {
    private Integer taskId;
    private Integer courseId;
    private List<Integer> solutionsId;
}
@Data
class GroupTask {
    private Integer groupId;
    private Integer taskId;
}
@Data
class GroupCourse {
    private Integer courseId;
    private Integer groupId;
}
