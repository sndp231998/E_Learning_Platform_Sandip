package com.e_learning.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DailyBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private LocalDateTime date;
    
    private double profit;  // Carry forward profit
    private double loss;  // Carry forward loss//private double closingBalance;
    
    //private boolean iisPositive;
    
    private double totalincome;
    private double totalexpense;
}
