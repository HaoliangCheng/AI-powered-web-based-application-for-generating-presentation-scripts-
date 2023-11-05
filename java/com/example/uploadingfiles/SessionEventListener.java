package com.example.uploadingfiles;

import java.nio.file.Path;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import com.example.uploadingfiles.storage.FileSystemStorageService;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

@Component
public class SessionEventListener implements HttpSessionListener  {
	
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		System.out.println("session done");
		deleteDirectory();
	}

	private void deleteDirectory() {
		Path userCache = FileSystemStorageService.userCacheGetter();
		FileSystemUtils.deleteRecursively(userCache.toFile());
		ImageService.deleteimages();
	}
	
	
}
