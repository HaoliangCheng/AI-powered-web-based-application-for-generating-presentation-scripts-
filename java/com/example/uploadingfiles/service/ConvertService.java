package com.example.uploadingfiles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.uploadingfiles.database.SlidesToScript;
import com.example.uploadingfiles.database.User;
import com.example.uploadingfiles.repositories.SlidesRepo;
import com.example.uploadingfiles.repositories.UserRepository;

@Service
//Database SlidesToTxt entity operations
public class ConvertService {
	
	private final SlidesRepo slidesRepo;
	private final UserRepository userRepository;
	
	@Autowired
	public ConvertService(SlidesRepo slidesRepo, UserRepository userRepository) {
		this.slidesRepo = slidesRepo;
		this.userRepository = userRepository;
	}
	
	public SlidesToScript createEntity(Long userId, SlidesToScript entity) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found!"));
		user.getEntities().add(entity);
//		userRepository.save(user);
		
		entity.setUser(user);
		return slidesRepo.save(entity);
	}
	
	public SlidesToScript save(SlidesToScript entity) {
		return slidesRepo.save(entity);
	}
	
	
	public void deleteEntity(Long id) {
		slidesRepo.deleteById(id);
	}
	
	public void deleteAll() {
		slidesRepo.deleteAll();
	}
	
}
