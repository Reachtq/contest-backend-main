package com.group.contestback.services;

import com.group.contestback.models.Lectures;
import com.group.contestback.repositories.LecturesRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LecturesServiceClass implements LecturesService{
    private final LecturesRepo lecturesRepo;


    @Override
    public void addLecture(String name, String description, Integer courseId, MultipartFile file) throws IOException {
        Lectures lectures = new Lectures();
        lectures.setName(name);
        lectures.setDescription(description);
        lectures.setCourseId(courseId);
        lectures.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        lectures.setContentType(file.getContentType());
        lectures.setData(file.getBytes());
        lectures.setSize(file.getSize());

        lecturesRepo.save(lectures);
    }

    @Override
    public List<Lectures> getLecturesByCourse(Integer courseId) {
        return lecturesRepo.getLecturesByCourseId(courseId);
    }

    @Override
    public Optional<Lectures> getLecture(Integer id) {
        return lecturesRepo.findById(id);
    }

    @Override
    public void updateLecture(Lectures lecture, MultipartFile file) throws IOException {
        Lectures lectureOld = lecturesRepo.getById(lecture.getId());
        lectureOld.setName(lecture.getName());
        lectureOld.setDescription(lecture.getDescription());
        lectureOld.setCourseId(lecture.getCourseId());
        lectureOld.setFileName(StringUtils.cleanPath(file.getOriginalFilename()));
        lectureOld.setContentType(file.getContentType());
        lectureOld.setData(file.getBytes());
        lectureOld.setSize(file.getSize());

        lecturesRepo.save(lectureOld);
    }
    @Override
    public void deleteLectureById(Integer id) {
        lecturesRepo.deleteById(id);
    }
}
