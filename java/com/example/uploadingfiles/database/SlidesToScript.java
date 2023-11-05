package com.example.uploadingfiles.database;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "entities")

public class SlidesToScript {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	private String pdfName;
	private String pdfAddr;
	private String chatGPTName;
	private String chatGptAddr;
	
	public String getChatGptAddr() {
		return chatGptAddr;
	}

	public void setChatGptAddr(String chatGptAddr) {
		this.chatGptAddr = chatGptAddr;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	
	public SlidesToScript() {
		
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPdfName() {
		return pdfName;
	}

	public void setPdfName(String pdfName) {
		this.pdfName = pdfName;
	}

	public String getPdfAddr() {
		return pdfAddr;
	}

	public void setPdfAddr(String pdfAddr) {
		this.pdfAddr = pdfAddr;
	}

	public SlidesToScript(String pdfName) {
		this.pdfName = pdfName;
	}


	public void setUser(User user) {
		this.user = user;
	}


}
