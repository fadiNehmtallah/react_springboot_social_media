package com.example.mongodb.objects;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class User {
	@Id
	private long userId;
	private Date createdAt;
	private String email;
	private String handle;
	private String token;
	private String password;
	private String confirmPassword;
	private String location;
	private String bio;
	private String website;
	private String imageUrl;
	

	public static final String SEQUENCE_NAME = "user_sequence";

	public User(String email, String handle, String password, String confirmPassword) {
		int x1 = (int) Math.floor(Math.random()*1000000000);
		int x2 = (int) Math.floor(Math.random()*1000000000);
		this.token = "Bearer "+x1 + email + x2 + handle;
		this.createdAt = new Date();
		this.email = email;
		this.handle = handle;
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.imageUrl = "no-img.png";
	}

	public long getUserId() {
		return this.userId;
	}

	public void setUserId(long l) {
		this.userId = l;
	}



	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return this.confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}


	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHandle() {
		return this.handle;
	}

	public void setHandle(String handle) {
		this.handle = handle;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBio() {
		return this.bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public String getWebsite() {
		return this.website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getImageUrl() {
		return this.imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	

	
	
}