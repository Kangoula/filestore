package org.filestore.web;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/api/*")
public class FileStoreApplication extends Application {
	
	private HashSet<Class<?>> classes = new HashSet<Class<?>>();

	public FileStoreApplication() {
		classes.add(FileItemsResource.class);
		classes.add(FileMetricsResource.class);
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		HashSet<Object> set = new HashSet<Object>();
		return set;
	}
}