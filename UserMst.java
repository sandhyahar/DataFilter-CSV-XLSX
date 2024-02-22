package com.API.Document;

//import org.springframework.data.annotation.Id;
//import org.springframework.data.elasticsearch.annotations.Field;
//
//import org.springframework.data.elasticsearch.annotations.FieldType;


public class UserMst {

//	@Id
	private String id;

//	@Field(type = FieldType.Text, name = "userId")
	private String userId;

//	@Field(type = FieldType.Text, name = "emailId")
	private String emailId;

//	@Field(type = FieldType.Text, name = "mobileNo")
	private String mobileNo;

//	@Field(type = FieldType.Integer, name = "userType")
	private int userType;
	
	private String password;
	
	private String firstName;
	
	private String lastName;

	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getUserType() {
		return userType;
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

}
