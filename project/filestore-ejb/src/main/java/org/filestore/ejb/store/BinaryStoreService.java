package org.filestore.ejb.store;

import org.jboss.resource.adapter.jdbc.remote.SerializableInputStream;

import java.io.InputStream;

public interface BinaryStoreService {
	
	public boolean exists(String key) throws BinaryStoreServiceException;
	
	public String put(SerializableInputStream is) throws BinaryStoreServiceException;
	
	public InputStream get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;
	
	public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException;

}
