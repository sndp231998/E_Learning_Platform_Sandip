package com.e_learning.payloads;

import java.util.List;

import lombok.Data;

@Data
public class UserFacultyDto {


	private int id;
	private String name;
    private List<String> facult;
    private List<String> roles;
    
    public UserFacultyDto(int id, String name, List<String> facult, List<String> roles) {
        this.id = id;
        this.facult = facult;
        this.roles = roles;
        this.name=name;
    }
}
