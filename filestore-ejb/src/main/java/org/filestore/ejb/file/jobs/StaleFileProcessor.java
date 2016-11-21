package org.filestore.ejb.file.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.security.RunAs;
import javax.batch.api.chunk.ItemProcessor;
import javax.ejb.EJB;
import javax.inject.Named;

import org.filestore.api.FileItem;
import org.filestore.api.FileServiceAdmin;
import org.filestore.api.FileServiceException;
import org.filestore.ejb.file.entity.FileItemEntity;

@Named(value="staleFileProcessor")
@RunAs("system")
public class StaleFileProcessor implements ItemProcessor {

	private static final Logger LOGGER = Logger.getLogger(StaleFileProcessor.class.getName());
	
	@EJB
	private FileServiceAdmin service; 
	
	public StaleFileProcessor() {
    	LOGGER.log(Level.INFO, "stale file processor instanciated");
    }

	@Override
	public String processItem(Object item) {
		LOGGER.log(Level.INFO, "processing file item : " + ((FileItemEntity)item).getId());
		try {
			service.deleteFile(((FileItem)item).getId());
			return ((FileItem)item).getId();
		} catch ( FileServiceException e ) {
			LOGGER.log(Level.INFO, "unable to process file item : " + ((FileItemEntity)item).getId(), e);
			return null;
		}
	}

}
