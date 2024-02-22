package com.API.pojo;

import javax.persistence.Id;

public class ApiKeyMst {
	
	private String srno;
	private String userId;
	private String password;
	private String token;
	private String tokenTime;
	
	@Id
	public String getSrno() {
		return srno;
	}
	public void setSrno(String srno) {
		this.srno = srno;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getTokenTime() {
		return tokenTime;
	}
	public void setTokenTime(String tokenTime) {
		this.tokenTime = tokenTime;
	}
	
		
	
	

}
