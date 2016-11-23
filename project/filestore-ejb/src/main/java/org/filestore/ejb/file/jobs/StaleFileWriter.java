package org.filestore.ejb.file.jobs;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

@Named(value="staleFileWriter")
public class StaleFileWriter extends AbstractItemWriter {

	private static final Logger LOGGER = Logger.getLogger(StaleFileWriter.class.getName());
	
	public StaleFileWriter() {
    	LOGGER.log(Level.INFO, "stale file writer instanciated");
    }

	@Override
	public void writeItems(List<Object> items) throws Exception {
		for ( Object item : items ) {
			LOGGER.log(Level.INFO, "item deleted: " + item);
		}
	}

}
