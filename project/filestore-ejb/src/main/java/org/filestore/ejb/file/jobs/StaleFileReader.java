package org.filestore.ejb.file.jobs;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemReader;
import javax.ejb.EJB;
import javax.inject.Named;

import org.filestore.api.FileItem;
import org.filestore.api.FileServiceAdmin;
import org.filestore.api.FileServiceException;

@Named(value="staleFileReader")
public class StaleFileReader extends AbstractItemReader {
	
	private static final Logger LOGGER = Logger.getLogger(StaleFileReader.class.getName());
	
	@EJB
	private FileServiceAdmin admin; 

    public StaleFileReader() {
    	LOGGER.log(Level.INFO, "stale file reader instanciated");
    }

    @Override
    public FileItem readItem() {
        LOGGER.log(Level.INFO, "reading next stale file item");
        try {
			return admin.getNextStaleFile();
		} catch (FileServiceException e) {
			LOGGER.log(Level.INFO, "unable to read next stale file item", e);
		}
        return null;
    }
}