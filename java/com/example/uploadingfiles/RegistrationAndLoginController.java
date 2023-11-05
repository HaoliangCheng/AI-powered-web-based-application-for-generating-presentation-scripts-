package com.example.uploadingfiles;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.uploadingfiles.database.User;
import com.example.uploadingfiles.service.MyUserDetailService;
import com.example.uploadingfiles.service.RegistrationVerification;
import com.example.uploadingfiles.storage.FileSystemStorageService;

import jakarta.servlet.http.HttpSession;

@Controller
public class RegistrationAndLoginController {
	
	private final RegistrationVerification registrationVerification;
	private final MyUserDetailService myUserDetailService;
	private final FileSystemStorageService fileSystemStorageService;
	
	@Autowired
	public RegistrationAndLoginController(RegistrationVerification registrationVerification, MyUserDetailService myUserDetailService, FileSystemStorageService fileSystemStorageService) {
		this.registrationVerification = registrationVerification;
		this.myUserDetailService = myUserDetailService;
		this.fileSystemStorageService = fileSystemStorageService;
	}
	
	@GetMapping("/register")
	public String showRegistrationForm() {
		return "register";
	}
	
	@PostMapping("/register")
	public String register(@RequestParam("username") String username, @RequestParam("password") String password,
			RedirectAttributes redirectAttributes) {
		User user = myUserDetailService.findByUsername(username);

		if (user != null) {
			redirectAttributes.addFlashAttribute("message", "Username already exits.");
			// 添加一个error页面
			return "redirect:/register";
		}
		
		User newUser = new User();
		newUser.setUsername(username);
		newUser.setEmail(username);
		newUser.setPassword(password);
		myUserDetailService.save(newUser);
		
		System.out.println("aaaaa");
		registrationVerification.registerUser(newUser);
		System.out.println("bbbb");
		
		redirectAttributes.addFlashAttribute("registrationSymbol", true);

		return "redirect:/register";
	}
	

	@GetMapping("/login")
	public String showLoginForm() {

		return "login";
	}
	
	
	@GetMapping("/authorizedPage")
	public String uploadForm(Model model, HttpSession session) {
		String userName = getUsername();
		model.addAttribute("username", userName);
		session.setAttribute("username", userName);
				
		User user = myUserDetailService.findByUsername(userName);
		int entityNum = myUserDetailService.countUserEntities(user);
		String userNameNum = userName + "_" + entityNum;
		fileSystemStorageService.setCurrentUserIdentifier(userNameNum);
		return "authorizedPage";
	}
	
	
	@GetMapping("/verify")
	public String verifyUser(@RequestParam("id") Long id, @RequestParam("Token") String token) {
		User user = myUserDetailService.findById(id).orElse(null);
		
		if (user != null && user.getVerificationToken().equals(token)) {
			user.setEnable(true);
			myUserDetailService.saveWithoutPassword(user);
			return "redirect:/verificationSuccess";
		} else {
			return "verificationFailure"; 
		}
	}
	
	@GetMapping("/verificationSuccess")
	public String verificationSuccess() {
		
		return "verificationSuccess";
	}
	
	public String getUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
			String currentUserName = authentication.getName();
			return currentUserName;
		}
		return null; // Or throw an exception, or a custom message
	}

}
