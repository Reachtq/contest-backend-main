package com.group.contestback.controllers;

import com.group.contestback.models.Lectures;
import com.group.contestback.repositories.LecturesRepo;
import com.group.contestback.responseTypes.LecturesResponse;
import com.group.contestback.services.LecturesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.FileEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;
import java.util.stream.Collectors;

@Api(tags = {"Lectures controller"})
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
@RequestMapping("/lecture")
public class LecturesController {
    private final LecturesService lecturesService;

    @ApiOperation(value = "Добавляет новую лекцию")
    @PostMapping("/addLecture")
    public ResponseEntity<?> addLecture(@ModelAttribute LectureForm lectureForm){
        try {
            lecturesService.addLecture(lectureForm.getName(), lectureForm.getDescription(),
                    lectureForm.getCourseId(), lectureForm.getFile());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", lectureForm.getFile().getOriginalFilename()));
        } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(String.format("Could not upload the file: %s!", lectureForm.getFile().getOriginalFilename()));
        }
    }

    @ApiOperation(value = "Возращает все лекции к курсу")
    @GetMapping("/getLecturesToCourse/{toCourseId}")
    public ResponseEntity<?> getAllLecturesToCourse(@PathVariable String toCourseId) {
        return ResponseEntity.ok().body(lecturesService.getLecturesByCourse(Integer.parseInt(toCourseId))
                .stream()
                .map(this::mapToFileResponse)
                .collect(Collectors.toList()));
    }

    private LecturesResponse mapToFileResponse(Lectures lectures) {
        String downloadURL = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/lecture/getLecture/")
                .path(String.valueOf(lectures.getId()))
                .toUriString();
        LecturesResponse lecturesResponse = new LecturesResponse();
        lecturesResponse.setId(String.valueOf(lectures.getId()));
        lecturesResponse.setName(lectures.getName());
        lecturesResponse.setContentType(lectures.getContentType());
        lecturesResponse.setSize(lectures.getSize());
        lecturesResponse.setUrl(downloadURL);

        return lecturesResponse;
    }

    @ApiOperation(value = "Возращает лекцию по id")
    @GetMapping("/getLecture/{id}")
    public ResponseEntity<?> getFile(@PathVariable Integer id) {
        Optional<Lectures> lecturesOptional = lecturesService.getLecture(id);

        if (!lecturesOptional.isPresent()) {
            return ResponseEntity.notFound()
                    .build();
        }

        Lectures lectures = lecturesOptional.get();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + lectures.getName() + "\"")
                .contentType(MediaType.valueOf(lectures.getContentType()))
                .body(lectures.getData());
    }

    @ApiOperation(value = "Редактирование лекции")
    @PostMapping("/updateLecture")
    public ResponseEntity<?> updateLecture(@RequestBody Lectures lecture, @RequestBody MultipartFile file){
        try {
            lecturesService.updateLecture(lecture, file);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(String.format("File uploaded successfully: %s", file.getOriginalFilename()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(String.format("Could not upload the file: %s!", file.getOriginalFilename()));
        }
    }

    @ApiOperation(value = "Удаление лекции по id")
    @DeleteMapping("/deleteLecture/{lectureId}")
    public ResponseEntity<?> deleteLecture(@RequestBody Integer lectureId){
        lecturesService.deleteLectureById(lectureId);
        return ResponseEntity.ok().build();
    }
}

@Data
class LectureForm {
    private String name;
    private String description;
    private Integer courseId;
    private MultipartFile file;
}
