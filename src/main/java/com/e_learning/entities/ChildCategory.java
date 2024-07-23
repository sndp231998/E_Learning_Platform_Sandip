//package com.e_learning.entities;
//
//import lombok.Getter;
//import lombok.Setter;
//
//import javax.persistence.CascadeType;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.JoinColumn;
//import javax.persistence.ManyToOne;
//import javax.persistence.Table;
//
//@Entity
//@Table(name = "child_categories")
//@Getter
//@Setter
//public class ChildCategory {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer ch_categoryId;
//    
//    @Column(name = "title", length = 100, nullable = false)
//    private String categoryTitle;
//    
//    @Column(name = "description")
//    private String categoryDescription;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private Category category;
//}
