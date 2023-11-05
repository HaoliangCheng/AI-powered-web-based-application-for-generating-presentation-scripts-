package com.example.uploadingfiles;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.example.uploadingfiles.database.SlidesToScript;
import com.example.uploadingfiles.service.ConvertService;
import com.example.uploadingfiles.storage.StorageProperties;
import com.example.uploadingfiles.storage.StorageService;

import jakarta.servlet.http.HttpSessionListener;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class UploadingFilesApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadingFilesApplication.class, args);
		
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			ImageService.deleteimages();
			storageService.init();
		};
	}
	
	@Bean 
	public ServletListenerRegistrationBean<HttpSessionListener>essionListner(){
		ServletListenerRegistrationBean<HttpSessionListener> listenerBean = 
				new ServletListenerRegistrationBean<>();
		
		listenerBean.setListener(new SessionEventListener());
		return listenerBean;
	}

}
