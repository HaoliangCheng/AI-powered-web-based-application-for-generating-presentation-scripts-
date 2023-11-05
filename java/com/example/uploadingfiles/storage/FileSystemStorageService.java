package com.example.uploadingfiles.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;
	private static Path userCache;
	private String currentUserIdentifier;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		this.rootLocation = Paths.get(properties.getLocation());
		FileSystemStorageService.userCache = Paths.get(properties.getCacheStr());
	}
	
	public static Path userCacheGetter() {
		return userCache;
	}
	public Path getRootLocation() {
		return rootLocation;
	}

	@Override
	public Path[] store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}
			Path destinationFile = this.rootLocation.resolve(
					Paths.get(file.getOriginalFilename()))
					.normalize().toAbsolutePath();
			
			
			// store pptx storage address and parent folder address
			Path[] paths = new Path[2];
			paths[0] = destinationFile;
			paths[1] = this.rootLocation.toAbsolutePath();
			// 
			System.out.println(destinationFile.toString() + " in store()");
			System.out.println(this.rootLocation.toAbsolutePath().toString() + " in store()");
			//
			
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				// This is a security check
				throw new StorageException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
				
			}
			return paths;
		}
		catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}


	@Override
	public Stream<Path> loadAll() {
		
		try {
			
			Path userFolder = this.rootLocation.resolve(currentUserIdentifier);
			
			if (!Files.exists(userFolder)) {
	            Files.createDirectories(userFolder);
	        }
			
			// print out all txt files in the directory
			Files.walk(userFolder, 1)
			.filter(path -> !path.equals(userFolder))
			.map(userFolder::relativize).
			forEach(System.out::println);
			//
			
			return Files.walk(userFolder, 1)
				.filter(path -> !path.equals(userFolder))
				.map(userFolder::relativize);
		}
		catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		Path userFolder = rootLocation.resolve(currentUserIdentifier);
		
		return userFolder.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			
			Resource resource = new UrlResource(file.toUri());
			
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
		FileSystemUtils.deleteRecursively(userCache.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
			Files.createDirectories(userCache);
			
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	public void setCurrentUserIdentifier(String userNameNum) {
		this.currentUserIdentifier = userNameNum;
	}
}
