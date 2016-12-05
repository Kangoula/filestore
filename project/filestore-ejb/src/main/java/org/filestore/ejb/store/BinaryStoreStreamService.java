package org.filestore.ejb.store;

import org.filestore.api.FileData;



public interface BinaryStoreStreamService {
	
	public boolean exists(String key) throws BinaryStoreServiceException;
	
	public String put(FileData is) throws BinaryStoreServiceException;
	
	public FileData get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;
	
	public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;

}
