package org.filestore.ejb.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FileStoreConfig {
	
	private static final String BASE_DOWNLOAD_URL = "http://localhost:8080/api/files/";
	private static Path home = null;
	static {
		if ( System.getenv("FILESTORE_HOME") != null ) {
	    	home = Paths.get(System.getenv("FILESTORE_HOME"));
	    } else if ( System.getProperty("filestore.home") != null ) {
	    	home = Paths.get(System.getProperty("filestore.home"));
	    } else {
	    	home = Paths.get(System.getProperty("user.home"), ".filestore");
	    }
	}
	
	public static Path getHome() {
		return home;
	}
	
	public static String getDownloadBaseUrl() {
		return BASE_DOWNLOAD_URL;
	}

}
