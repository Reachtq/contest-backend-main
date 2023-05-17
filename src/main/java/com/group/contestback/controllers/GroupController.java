package com.group.contestback.controllers;

import com.group.contestback.models.Groups;
import com.group.contestback.models.Tasks;
import com.group.contestback.services.GroupService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(tags = {"Group controller"})
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/group")
public class GroupController {
    private final GroupService groupService;

    @ApiOperation(value = "Добавляет новую группу")
    @PostMapping("/addGroup") public ResponseEntity<?> addGroup(@RequestBody addGroupForm form) {
        groupService.addGroup(form.getNumber(), form.getYear());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Редактирование группы")
    @PostMapping("/updateGroup")
    public ResponseEntity<?> updateGroup(@RequestBody Groups group) {
        groupService.updateGroup(group);
        return ResponseEntity.ok().build();
    }
    @ApiOperation(value = "Возращает все группы")
    @GetMapping("/allGroups")
    public ResponseEntity<?> getAllGroups() {
        return ResponseEntity.ok().body(groupService.getAllGroups());
    }

    @ApiOperation(value = "Возращает таблицу со всеми группами и их курсами")
    @GetMapping("/allGroupsCourses")
    public ResponseEntity<?> getAllGroupsCourses() {
        return ResponseEntity.ok().body(groupService.getAllGroupCourses());
    }

    @ApiOperation(value = "Добавляет группу на курс")
    @PostMapping("/addGroupOnCourse")
    public ResponseEntity<?> addGroupOnCourse(@RequestBody addGroupOnCourse form) {
        groupService.addGroupOnCourse(form.getCourseId(), form.getGroupId());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Удаление группы с курса")
    @DeleteMapping("/deleteGroupFromCourse/{groupCourseId}")
    public ResponseEntity<?> deleteGroupFromCourse(@PathVariable String groupCourseId) {
        groupService.deleteGroupFromCourseById(Integer.parseInt(groupCourseId));
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Удаление группы")
    @DeleteMapping("/deleteGroup/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable String groupId) {
        groupService.deleteGroupById(Integer.parseInt(groupId));
        return ResponseEntity.ok().build();
    }
}

@Data
class addGroupForm {
    private String number;
    private Integer year;
}

@Data
class addGroupOnCourse {
    private Integer courseId;
    private Integer groupId;
}