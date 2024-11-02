package com.e_learning.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer markId;

    private Integer score; // Stores the score/mark for the exam

    @ManyToOne
    @JoinColumn(name = "exam_id")
    @JsonIgnore 
    private Exam exam;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore 
    private User user;

    // Other fields can be added if necessary, like `feedback`, `gradedDate`, etc.
}

