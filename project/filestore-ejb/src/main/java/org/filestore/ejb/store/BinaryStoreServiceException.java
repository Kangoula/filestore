package org.filestore.ejb.store;

public class BinaryStoreServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public BinaryStoreServiceException() {
		super();
	}

	public BinaryStoreServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public BinaryStoreServiceException(String message) {
		super(message);
	}

	public BinaryStoreServiceException(Throwable cause) {
		super(cause);
	}

}
