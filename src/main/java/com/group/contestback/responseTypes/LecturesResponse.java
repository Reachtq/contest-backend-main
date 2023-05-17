package com.group.contestback.responseTypes;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecturesResponse {
    private String id;

    private String name;

    private String description;

    private Integer courseId;

    private String fileName;

    private String contentType;

    private Long size;

    private String url;

    public LecturesResponse(String name, String description, Integer courseId, String fileName, String contentType, Long size, String url){
        this.name = name;
        this.description = description;
        this.courseId = courseId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.url = url;

    }
}
