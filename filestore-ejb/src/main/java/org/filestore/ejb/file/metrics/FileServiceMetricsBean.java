package org.filestore.ejb.file.metrics;

import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.filestore.api.FileServiceException;
import org.filestore.api.FileServiceMetrics;

@Startup
@Stateless(name = "fileservicemetrics")
@Local(FileServiceMetrics.class)
public class FileServiceMetricsBean implements FileServiceMetrics {
	
	private static final Logger LOGGER = Logger.getLogger(FileServiceMetricsBean.class.getName());
	
	private static long start = System.currentTimeMillis();
	private static int uploads = 0;
	private static int downloads = 0;
	
	@AroundInvoke
	public Object intercept(InvocationContext ic) throws Exception {
		LOGGER.entering(ic.getTarget().toString(), ic.getMethod().getName());
		try {
			Object obj =  ic.proceed();
			if ( ic.getMethod().getName().equals("getFileContent") ) {
				downloads++;
			}
			if ( ic.getMethod().getName().equals("getWholeFileContent") ) {
				downloads++;
			}
			if ( ic.getMethod().getName().equals("postFile") ) {
				uploads++;
			}
			return obj;
		} finally {
			LOGGER.exiting(ic.getTarget().toString(), ic.getMethod().getName());
		}
	}

	@Override
	public int getTotalUploads() throws FileServiceException {
		return uploads;
	}

	@Override
	public int getTotalDownloads() throws FileServiceException {
		return downloads;
	}

	@Override
	public int getUptime() throws FileServiceException {
		return (int) (System.currentTimeMillis() - start);
	}

}
