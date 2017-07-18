package com.multitv.yuv.gmaillogin;

/**
 * Callback to listen the event occur inside the Gmail Login Async Task.<br>
 * All callback are occur in back thread
 */
interface OnGmailLoginAsyncTaskListener {
	/**
	 * Method to show the Message
	 * 
	 * @param msg
	 */
	void onGmailLoginAsyncTaskListenerShowMesage(String msg);

	/**
	 * Method to notify the user data that was received after loging, It is Run
	 * on Caller Thread
	 * 
	 * @param obj
	 *            Object of MKRException/ProilePojo
	 */
	void onGmailLoginAsyncTaskListenerUserData(Object obj);
}
