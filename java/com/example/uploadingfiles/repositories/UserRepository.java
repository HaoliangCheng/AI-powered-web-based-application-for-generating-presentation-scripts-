package com.example.uploadingfiles.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.uploadingfiles.database.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByUsername(String username);
	
	Optional<User> findById(Long id);
}
