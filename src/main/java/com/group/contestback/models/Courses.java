package com.group.contestback.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Courses {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private Integer year;
    @Column(name="teacherid")
    private Integer teacherId;
//    private Boolean deleted;

    public Courses(String name, Integer year, Integer teacherId) {
        this.name = name;
        this.year = year;
        this.teacherId = teacherId;
//        this.deleted = false;
    }
}
