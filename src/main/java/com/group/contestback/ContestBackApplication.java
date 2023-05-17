package com.group.contestback;

import com.group.contestback.models.AppUser;
import com.group.contestback.models.Roles;
import com.group.contestback.repositories.RolesRepo;
import com.group.contestback.services.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class ContestBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContestBackApplication.class, args);
    }

    @Bean
    CommandLineRunner run(AppUserService userService, RolesRepo repo) {
        return args -> {
            if (userService.getUsers().size() == 0) {
                AppUser user = new AppUser(1, "Ivan", "ivan", "ivanovich",
                        "ivanLogin", "secpass444555.", "secpass444555.", "reacht@bk.ru", 1, 1);
                userService.saveAppUser(user);
                Roles roleAdmin = new Roles(1, "ROLE_ADMIN", "АДМИНИСТРАТОР");
                Roles roleTeacher = new Roles(2, "ROLE_TEACHER", "ПРЕПОДАВАТЕЛЬ");
                Roles roleAssistantTeacher = new Roles(3, "ROLE_ASSISTANT_TEACHER", "АССИСТЕНТ ПРЕПОДАВАТЕЛЯ");
                Roles roleUser = new Roles(4, "ROLE_USER", "ПОЛЬЗОВАТЕЛЬ");
                repo.save(roleAdmin);
                repo.save(roleTeacher);
                repo.save(roleAssistantTeacher);
                repo.save(roleUser);
            }
        };
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
