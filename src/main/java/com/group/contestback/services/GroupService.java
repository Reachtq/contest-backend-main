package com.group.contestback.services;

import com.group.contestback.models.Groups;
import com.group.contestback.responseTypes.GroupCoursesWithNames;

import java.util.List;

public interface GroupService {
    void addGroup(String number, Integer year);
    void updateGroup(Groups group);
    List<Groups> getAllGroups();
    List<GroupCoursesWithNames> getAllGroupCourses();
    void addGroupOnCourse(Integer courseId, Integer groupId);
    void deleteGroupFromCourseById(Integer groupCourseId);
    void deleteGroupById(Integer groupId);
}
