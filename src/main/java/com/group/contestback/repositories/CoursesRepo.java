package com.group.contestback.repositories;

import com.group.contestback.models.Courses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoursesRepo extends JpaRepository<Courses, Integer> {
    Optional<Courses> findById(Integer id);
    List<Courses> findByTeacherId(Integer teacherId);

//    List<Courses> findAllCourseAndDeletedIsFalse();
//    List<Courses> findByTeacherId(Integer teacherId);
}
