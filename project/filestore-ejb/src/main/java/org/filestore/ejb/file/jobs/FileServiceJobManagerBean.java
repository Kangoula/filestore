package org.filestore.ejb.file.jobs;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RunAs;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.ejb3.annotation.SecurityDomain;

@Stateless(name = "filejobmanager")
@SecurityDomain("filestore")
@RunAs("system")
public class FileServiceJobManagerBean {
	
	private static final Logger LOGGER = Logger.getLogger(FileServiceJobManagerBean.class.getName());

	@Schedule(minute="*", hour="*/24")
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void cleanExpiredFiles() {
		LOGGER.log(Level.INFO, "Clean Expired File called");
		JobOperator jo = BatchRuntime.getJobOperator();
        long jid = jo.start("purge", new Properties());
        LOGGER.log(Level.INFO, "batch job started with id: " + jid);
        LOGGER.log(Level.INFO, "batch job execution status: " + jo.getJobExecution(jid).getBatchStatus());
	}
	
}
