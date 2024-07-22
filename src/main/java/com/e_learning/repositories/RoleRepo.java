package com.e_learning.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Role;


public interface RoleRepo  extends JpaRepository<Role, Integer>{

}
