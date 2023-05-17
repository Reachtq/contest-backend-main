package com.group.contestback.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lectures {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;

    private String description;

    @Column(name="courseid")
    private Integer courseId;

    private String fileName;

    private String contentType;

    private Long size;

    @Lob
    private byte[] data;

    public Lectures(String name, String description, Integer courseId, String fileName, String contentType, Long size, byte[] data){
        this.name = name;
        this.description = description;
        this.courseId = courseId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = size;
        this.data = data;

    }
}
