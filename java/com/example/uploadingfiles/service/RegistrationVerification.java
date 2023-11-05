package com.example.uploadingfiles.service;

import java.util.UUID;

import com.example.uploadingfiles.database.User;
import com.example.uploadingfiles.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class RegistrationVerification {
	@Autowired
	private UserRepository userRepository;
	private String URL= "http://localhost:8080";
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	public void registerUser(User user) {
		String verificationToken = UUID.randomUUID().toString();
		user.setVerificationToken(verificationToken);
		
		//save the user to the database
		user.setEnable(false);
		userRepository.save(user);
		
		//send a verification email
		sendVerificationEmail(user);
		
	}

	private void sendVerificationEmail(User user) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Please Verify Your Email for PresentPal");
		message.setText("Please click the following link to verify: \n\n" 
				+ URL+ "/verify?id=" + user.getId() + "&token=" + user.getVerificationToken());
		
		javaMailSender.send(message);
		
	}
}
