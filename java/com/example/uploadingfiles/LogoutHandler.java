package com.example.uploadingfiles;

import java.io.IOException;
import java.nio.file.Path;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.example.uploadingfiles.storage.FileSystemStorageService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LogoutHandler implements LogoutSuccessHandler {
	
	@Override 
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		deleteDirectoryContents();
		try {
			response.sendRedirect("/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void deleteDirectoryContents() {
		Path userCache = FileSystemStorageService.userCacheGetter();
		FileSystemUtils.deleteRecursively(userCache.toFile());
		ImageService.deleteimages();
	}

}
