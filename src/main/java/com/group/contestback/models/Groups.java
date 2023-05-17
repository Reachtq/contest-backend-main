package com.group.contestback.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Groups {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String number;
    private Integer year;
//    private Boolean deleted;

    public Groups(String number, Integer year) {
        this.number = number;
        this.year = year;
//        this.deleted = false;
    }
    public Groups(String number, String year) {
        this.number = number;
//        this.deleted = false;
    }
}
