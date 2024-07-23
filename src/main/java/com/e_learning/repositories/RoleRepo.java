package com.e_learning.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Role;


public interface RoleRepo  extends JpaRepository<Role, Integer>{
	Optional<Role> findByName(String name);
}
