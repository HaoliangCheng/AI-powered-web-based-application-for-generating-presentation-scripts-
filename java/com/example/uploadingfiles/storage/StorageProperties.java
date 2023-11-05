package com.example.uploadingfiles.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	private String location = "upload-dir";
	private String cache = "user-Cache";

	public String getLocation() {
		return location;
	}
	public String getCacheStr() {
		return cache;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
