package com.group.contestback.services;

import com.group.contestback.models.*;
import com.group.contestback.repositories.*;
import com.group.contestback.responseTypes.GroupCoursesWithNames;
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
public class GroupServiceClass implements GroupService{
    private final CoursesRepo coursesRepo;
    private final GroupsRepo groupsRepo;
    private final GroupCoursesRepo groupCoursesRepo;

    @Override
    public void addGroup(String number, Integer year) {
        Groups groups = new Groups(number, year);
        groupsRepo.save(groups);
    }

    @Override
    public void updateGroup(Groups group) {
        Groups groupOld = groupsRepo.getById(group.getId());
        groupOld.setNumber(group.getNumber());
        groupOld.setYear(group.getYear());
        groupsRepo.save(groupOld);
    }

    @Override
    public List<Groups> getAllGroups() {
        return groupsRepo.findAll();
    }

    @Override
    public List<GroupCoursesWithNames> getAllGroupCourses() {
        List<GroupCoursesWithNames> groupCoursesWithNames = new ArrayList<>();
        List<Groups> allGroups = groupsRepo.findAll();
//        groupCoursesRepo.findAll().forEach(groupCourses -> groupCoursesWithNames.add(new GroupCoursesWithNames(
//                        groupCourses.getId()
//                        , coursesRepo.getById(groupCourses.getCourseId()).getName(),
//                        groupCourses.getCourseId(),
//                        groupsRepo.getById(groupCourses.getGroupId()).getNumber(),
//                        groupCourses.getGroupId()
//                ))
//        );
        allGroups.forEach(group -> {
            GroupCoursesWithNames gwn = new GroupCoursesWithNames();
            gwn.setGroupId(group.getId());
            Groups group1 = groupsRepo.getById(group.getId());
            gwn.setGroupName(group1.getNumber());
            gwn.setGroupYear(group1.getYear());
            List<Courses> courses = new ArrayList<>();
            groupCoursesRepo.findAllByGroupId(group.getId()).forEach(groupCourses -> {
                Courses course = coursesRepo.findById(groupCourses.getCourseId()).get();
                courses.add(new Courses(course.getId(), course.getName(), course.getYear(), course.getTeacherId()));
            });
            gwn.setCourses(courses);
            groupCoursesWithNames.add(gwn);
        });
        return groupCoursesWithNames;
    }

    @Override
    public void addGroupOnCourse(Integer courseId, Integer groupId) {
        GroupCourses groupCourses = new GroupCourses(courseId, groupId);
        groupCoursesRepo.save(groupCourses);
    }

    @Override
    public void deleteGroupFromCourseById(Integer groupCourseId) {
        groupCoursesRepo.deleteById(groupCourseId);
    }

    @Override
    public void deleteGroupById(Integer groupId) {
        groupsRepo.deleteById(groupId);
    }
}
