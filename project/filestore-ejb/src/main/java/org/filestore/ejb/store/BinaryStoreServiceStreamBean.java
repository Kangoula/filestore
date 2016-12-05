package org.filestore.ejb.store;

import org.filestore.api.FileData;
import org.filestore.ejb.config.FileStoreConfig;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton(name="binarystore")
@Local(BinaryStoreService.class)
public class BinaryStoreServiceStreamBean implements BinaryStoreStreamService {

	private static final Logger LOGGER = Logger.getLogger(BinaryStoreServiceStreamBean.class.getName());

	public static final String DEFAULT_BINARY_HOME = "s3store";
	public static final int DISTINGUISH_SIZE = 2;

	private Path base;

	public BinaryStoreServiceStreamBean() {
	}

	@PostConstruct
	public void init() {
		this.base = Paths.get(FileStoreConfig.getHome().toString(), DEFAULT_BINARY_HOME);
		LOGGER.log(Level.FINEST, "Initializing service with base folder: " + base);
		try {
			Files.createDirectories(base);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "unable to initialize binary store", e);
		}
	}

	@Override
	public boolean exists(String key) throws BinaryStoreServiceException {
		Path file = Paths.get(base.toString(), key);
		return Files.exists(file);
	}

	@Override
	public String put(InputStream is) throws BinaryStoreServiceException {
		String key = UUID.randomUUID().toString();
		Path file = Paths.get(base.toString(), key);
		if ( Files.exists(file) ) {
			throw new BinaryStoreServiceException("unable to create file, key already exists");
		}
		try {
			Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new BinaryStoreServiceException("unexpected error during stream copy", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "unable to close stream", e);
			}
		}
		return key;
	}

	@Override
	public FileData get(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {
		Path file = Paths.get(base.toString(), key);
		if ( !Files.exists(file) ) {
			throw new BinaryStreamNotFoundException("file not found in storage");
		}
		try {
			return Files.newInputStream(file, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new BinaryStoreServiceException("unexpected error while opening stream", e);
		}
	}
	
	@Override
	public void delete(String key) throws BinaryStoreServiceException, BinaryStreamNotFoundException {
		Path file = Paths.get(base.toString(), key);
		if ( !Files.exists(file) ) {
			throw new BinaryStreamNotFoundException("file not found in storage");
		}
		try {
			Files.delete(file);
		} catch (IOException e) {
			throw new BinaryStoreServiceException("unexpected error while deleting stream", e);
		}
	}

}
