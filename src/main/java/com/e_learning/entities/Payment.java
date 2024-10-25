package com.e_learning.entities;

import java.time.LocalDateTime;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    private Integer total;
    private Integer totalPrice;
    private LocalDateTime addedDate;
    private String validDate;
    private String payment_screensort;;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    
    public enum PaymentStatus {
        PENDING, APPROVED, REJECTED
    }
    
    @ManyToMany
    @JoinTable(
        name = "payment_category",
        joinColumns = @JoinColumn(name = "payment_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}


