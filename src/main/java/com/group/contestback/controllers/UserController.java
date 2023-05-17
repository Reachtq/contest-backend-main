package com.group.contestback.controllers;

import com.group.contestback.common.GenerateUsersPasswordAndLoginXLSX;
import com.group.contestback.models.AppUser;
import com.group.contestback.repositories.GroupsRepo;
import com.group.contestback.responseTypes.UserPageResponse;
import com.group.contestback.services.AppUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Api(tags = {"User controller"})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class UserController {
    private final AppUserService userService;
    private final GroupsRepo groupsRepo;

    @ApiOperation(value = "Возращает всех пользователей")
    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @ApiOperation(value = "Возращает информацию пользователя")
    @GetMapping("/userInfo")
    public ResponseEntity<?> getUserInfo() {
        return ResponseEntity.ok().body(userService.getUserInfo());
    }

    @ApiOperation(value = "Возращает определенную страницу, определенного размера пользователей")
    @GetMapping("/usersPage/{page}/{pageSize}")
    public ResponseEntity<Page<UserPageResponse>> getUsersPage(@PathVariable String page, @PathVariable String pageSize) {
        return ResponseEntity.ok().body(userService.getUsersPage(Integer.parseInt(page), Integer.parseInt(pageSize)));
    }

    @ApiOperation(value = "Возращает определенную страницу, определенного размера пользователей")
    @GetMapping("/usersPageFind/{page}/{pageSize}/{str}")
    public ResponseEntity<Page<UserPageResponse>> getUsersPageFind(@PathVariable String page, @PathVariable String pageSize, @PathVariable String str) {
        return ResponseEntity.ok().body(userService.findUsersByLastNamePage(Integer.parseInt(page), Integer.parseInt(pageSize), str));
    }

    @ApiOperation(value = "Добавляет нового пользователя", notes = "Роли указывать необязательно, для этого существует другой запрос")
    @PostMapping("/user/add")
    public ResponseEntity<?> addUsers(@RequestBody UserRegistration user) {
        AppUser appUser = new AppUser(user.getFirstName(), user.getLastName(), user.getMiddleName(), user.getLogin(),
                user.getPassword(), "", user.getEmail(), 2, null);
        return ResponseEntity.ok().body(userService.saveAppUser(appUser));
    }

    @ApiOperation(value = "Добавляет новых пользователей из файла")
    @GetMapping("/user/add")
    public ResponseEntity<?> addUsersFromXLSX(@RequestParam("file") MultipartFile file,
                                              HttpServletResponse response) throws IOException {
        GenerateUsersPasswordAndLoginXLSX generateXlsxDocument = new GenerateUsersPasswordAndLoginXLSX(userService, groupsRepo);

        XSSFWorkbook workbook = generateXlsxDocument.readFromWorkbookAndSaveNewUser(file);
        response.setHeader("Content-Disposition", "attachment; filename=\"Passwords_and_Logins.xlsx\"");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        OutputStream outputStream = response.getOutputStream();

        workbook.write(outputStream);

        outputStream.flush();
        outputStream.close();
        workbook.close();

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Добавляет роль к пользователю")
    @PostMapping("/addrole")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) throws Exception {
        userService.addRoleToUser(form.getLogin(), form.getRoleName(), form.getDescription());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Изменяет группу пользователя")
    @PostMapping("/user/setgroup")
    public ResponseEntity<?> setUserGroup(@RequestBody GroupToUserForm form) {
        userService.setUserGroup(form.getLogin(), form.getGroupId());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Добавляет почту к пользователю")
    @PostMapping("/user/addEmail")
    public ResponseEntity<?> addEmailToUser(@RequestBody EmailToUserForm form) {
        userService.addEmailToUser(form.getLogin(), form.getEmail());
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Сброс пароля")
    @PostMapping("/user/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPassword form) {
        userService.resetPassword(form.getLogin(), form.getNewPassword());
        return ResponseEntity.ok().build();
    }
}

@Data
class UserRegistration {
    private String firstName;
    private String middleName;
    private String lastName;
    private String login;
    private String password;
    private String email;
}

@Data
class RoleToUserForm {
    private String login;
    private String roleName;
    private String description;
}

@Data
class EmailToUserForm {
    private String login;
    private String email;
}

@Data
class GroupToUserForm {
    private String login;
    private Integer groupId;
}

@Data
class PageSize {
    private Integer page;
    private Integer pageSize;
}

@Data
class PageSizeName {
    private Integer page;
    private Integer pageSize;
    private String str;
}

@Data
class ResetPassword {
    private String login;
    private String newPassword;
}

