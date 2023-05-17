package com.group.contestback.services;

import com.group.contestback.models.Courses;
import com.group.contestback.responseTypes.StudentTaskResponse;


import java.util.List;

public interface CourseService {
    void addCourse(String name, Integer year, Integer teacherId);
    void updateCourse(Courses course);
    List<Courses> getAllCourses();
//    List<Courses> getAllCoursesNew();
//    List<Courses> getCoursesToTeacher(Integer teacherId);
    List<StudentTaskResponse> getStudentCourses();
    List<Courses> getCoursesByTeacher(Integer teacherId);
    void deleteCourseById(Integer id);
//    void removeCourse(Integer courseId);
}
