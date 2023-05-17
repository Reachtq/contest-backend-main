package com.group.contestback.services;

import com.group.contestback.models.Courses;
import com.group.contestback.models.Groups;
import com.group.contestback.models.TaskTypes;
import com.group.contestback.models.Tasks;
import com.group.contestback.responseTypes.GroupCoursesWithNames;
import com.group.contestback.responseTypes.StudentTaskResponse;
import com.group.contestback.responseTypes.TaskResponse;

import java.util.List;

public interface TaskService {
    void addTaskType(String name);
    void addSolutionVariant(String solution, Boolean isAnswer, Integer taskId);
    List<TaskTypes> getTaskTypes();
    void addTask(String name, String solution, String description, Integer taskTypeId);
    void updateTask(Tasks task);
    List<Tasks> getTasks();
    List<TaskResponse> getTasksByCourse(Integer courseId);
    void addTaskToCourse(Integer taskId, Integer courseId);
//    List<Groups> getAllGroups();
//    void addGroup(String number, Integer year);
//    List<GroupCoursesWithNames> getAllGroupCourses();
//    void addGroupOnCourse(Integer courseId, Integer groupId);
    String getTaskDeadline(Integer taskId, Integer courseId);
    void addTaskDeadline(Integer taskId, Integer courseId, String deadline);
    TaskResponse getTask(Integer taskId, Integer courseId);
    TaskResponse getTask(Integer taskId);
    void deleteTaskFromCourseById(Integer taskCourseId);
    void deleteTaskById(Integer taskId);

//    List<StudentTaskResponse> getStudentCourses();
}
