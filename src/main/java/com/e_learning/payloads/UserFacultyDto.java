package com.e_learning.payloads;

import java.util.List;

import lombok.Data;

@Data
public class UserFacultyDto {


	private int id;
    private List<String> facult;
    
    public UserFacultyDto(int id, List<String> facult) {
        this.id = id;
        this.facult = facult;
    }
}
