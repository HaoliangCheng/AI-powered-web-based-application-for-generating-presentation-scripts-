package com.example.uploadingfiles.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.uploadingfiles.database.SlidesToScript;
import com.example.uploadingfiles.database.User;
import com.example.uploadingfiles.repositories.SlidesRepo;
import com.example.uploadingfiles.repositories.UserRepository;

@Service
public class MyUserDetailService implements UserDetailsService{

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final SlidesRepo slidesRepo;
	
	@Autowired
	public MyUserDetailService (UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, SlidesRepo slidesRepo) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.slidesRepo = slidesRepo;
	}
	
	public void save (User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
	
	public void saveWithoutPassword(User user) {
		userRepository.save(user);
	}
	
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = findByUsername(username);
		if (user != null) {
			return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}
	
	public int countUserEntities(User user) {
		return slidesRepo.countByUser(user);
	}
	
	// entity list
	public void showAllEntities(User user) {
		for (SlidesToScript e : user.getEntities()) {
			System.out.println(e.getPdfName());
		}
	}



}
