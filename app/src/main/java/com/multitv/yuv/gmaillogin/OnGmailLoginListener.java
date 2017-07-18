package com.multitv.yuv.gmaillogin;

/**
 * Callback to listen the event occur at the time of GMAIL Login
 */
public interface OnGmailLoginListener {
	/**
	 * Method to notify the exception occur at the time of GMAIL LOGIN
	 * 
	 * @param exception
	 */
	void onGmailLoginListenerException(Exception exception);

	/**
	 * Method to notify the user data that was received after loging. Run On
	 * Caller Thread
	 * 
	 * @param profilePojo
	 */
	void onGmailLoginListenerUserData(ProfilePojo profilePojo);
}
