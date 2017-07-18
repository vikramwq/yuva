package com.multitv.yuv.gmaillogin;

/**
 * Class to hold the data of an USER
 */
public class ProfilePojo {
	private String	name;
	private String	email;
	private String	picUrl;
	private String	gender;
	private String	bday;
	private String	gmailToken;
	private String	gmailRefreshToken;

	/**
	 * Method to get the display name of the user
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	void setName(String name) {
		this.name = name;
	}

	/**
	 * Method to get the email of the user
	 * 
	 * @return
	 */
	public String getEmail() {
		return email;
	}

	void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Method to get the pic url of the user
	 * 
	 * @return
	 */
	public String getPicUrl() {
		return picUrl;
	}

	void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	/**
	 * Method to get the gender of the user
	 * 
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * Method to get the birth date of the user
	 * 
	 * @return
	 */
	public String getBday() {
		return bday;
	}

	void setBday(String bday) {
		this.bday = bday;
	}

	/**
	 * Method to get the gmail auth token
	 * 
	 * @return
	 */
	public String getGmailToken() {
		return gmailToken;
	}

	void setGmailToken(String gmailToken) {
		this.gmailToken = gmailToken;
	}

	public String getGmailRefreshToken() {
		return gmailRefreshToken;
	}

	void setGmailRefreshToken(String gmailRefreshToken) {
		this.gmailRefreshToken = gmailRefreshToken;
	}

}
