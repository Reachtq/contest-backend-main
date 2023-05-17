package com.group.contestback.services;

import com.group.contestback.models.Lectures;
import com.group.contestback.repositories.LecturesRepo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface LecturesService {
    void addLecture(String name, String description, Integer courseId, MultipartFile file) throws IOException;
    List<Lectures> getLecturesByCourse(Integer courseId);
    Optional<Lectures> getLecture(Integer id);
    void updateLecture(Lectures lecture, MultipartFile file) throws IOException;
    void deleteLectureById(Integer id);
}
