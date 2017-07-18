package com.multitv.yuv.gmaillogin;

class MKRException extends Exception {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private String				mMessage;

	public MKRException(String message) {
		mMessage = message;
	}

	@Override
	public String getMessage() {
		return mMessage;
	}
}
