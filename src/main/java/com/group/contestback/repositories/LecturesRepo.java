package com.group.contestback.repositories;

import com.group.contestback.models.Lectures;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LecturesRepo extends JpaRepository<Lectures, Integer> {
    List<Lectures> getLecturesByCourseId(Integer toCourseId);

    void deleteAllByCourseId(Integer courseId);
}
