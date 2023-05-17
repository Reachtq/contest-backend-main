package com.group.contestback.controllers;

import com.group.contestback.models.Tasks;
import com.group.contestback.services.CourseService;
import com.group.contestback.models.Courses;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"Course controller"})
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/course")
public class CourseController {
    private final CourseService courseService;

    @ApiOperation(value = "Добавляет новый курс")
    @PostMapping("/addCourse")
    public ResponseEntity<?> addCourse(@RequestBody addCourseForm form) {
        courseService.addCourse(form.getName(), form.getYear(), form.getTeacherId());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Редактирование курса")
    @PostMapping("/updateCourse")
    public ResponseEntity<?> updateCourse(@RequestBody Courses course) {
        courseService.updateCourse(course);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Возращает все курсы")
    @GetMapping("/allCourses")
    public ResponseEntity<?> getAllCourses() {
        return ResponseEntity.ok().body(courseService.getAllCourses());
    }

    @ApiOperation(value = "Возращает все курсы по id преподавателя")
    @GetMapping("/getCourseToTeacher/{teacherId}")
    public ResponseEntity<?> getCoursesByTeacherId(@PathVariable String teacherId) {
        return ResponseEntity.ok().body(courseService.getCoursesByTeacher(Integer.parseInt(teacherId)));
    }

    @ApiOperation(value = "Возращает все курсы студента")
    @GetMapping("/studentCourses")
    public ResponseEntity<?> getAllStudentCourses() {
        return ResponseEntity.ok().body(courseService.getStudentCourses());
    }

//    @ApiOperation(value = "Возращает все курсы преподавателя")
//    @GetMapping("/getCourseToTeacher/{teacherId}")
//    public ResponseEntity<?> getAllCoursesToTeacherId(@PathVariable String teacherId) {
//        return ResponseEntity.ok().body(courseService.getCoursesToTeacher(Integer.parseInt(teacherId)));
//    }

    @ApiOperation(value = "Удаление курса")
    @DeleteMapping("/deleteCourse/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable String courseId) {
        courseService.deleteCourseById(Integer.parseInt(courseId));
        return ResponseEntity.ok().build();
    }
}

    @Data
    class addCourseForm {
        private String name;
        private Integer year;
        private Integer teacherId;
    }