package org.filestore.ejb.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.xml.ws.soap.MTOM;

import org.filestore.api.FileData;
import org.filestore.api.FileItem;
import org.filestore.api.FileService;
import org.filestore.api.FileServiceAdmin;
import org.filestore.api.FileServiceException;
import org.filestore.api.FileServiceLocal;
import org.filestore.ejb.file.entity.FileItemEntity;
import org.filestore.ejb.file.metrics.FileServiceMetricsBean;
import org.filestore.ejb.store.BinaryStoreService;
import org.filestore.ejb.store.BinaryStoreServiceException;
import org.filestore.ejb.store.BinaryStreamNotFoundException;
import org.jboss.ejb3.annotation.SecurityDomain;

@Stateless(name = "fileservice")
@Interceptors ({FileServiceMetricsBean.class})
@SecurityDomain("filestore")
@PermitAll
@MTOM(enabled=true)
@WebService(endpointInterface = "org.filestore.api.FileService")
@HandlerChain(file="/handler-chain.xml")
public class FileServiceBean implements FileService, FileServiceLocal, FileServiceAdmin {
	
	private static final Logger LOGGER = Logger.getLogger(FileServiceBean.class.getName());
	
	@PersistenceContext(unitName="filestore-pu")
	protected EntityManager em;
	@Resource
	protected SessionContext ctx;
	@EJB
	protected BinaryStoreService store;
	@Resource(mappedName = "java:jboss/exported/jms/topic/Notification")
	private Topic notificationTopic;
	@Inject 
	private JMSContext jmsctx;


	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String newFileId() throws FileServiceException {
		return store.genStreamId();
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String preparePostFile(String owner, List<String> receivers, String message)
			throws FileServiceException{
		LOGGER.log(Level.INFO, "PreoarePost File called (" + owner + ")");
		String id = newFileId();
		FileItemEntity file = new FileItemEntity();
		file.setId(id);
		file.setOwner(owner);
		file.setReceivers(receivers);
		file.setMessage(message);
		file.setPending(true);
		em.persist(file);

		return id;
	}



	@Override
	@WebMethod(operationName = "hasPendingFile")
	public boolean hasPendingFile(@WebParam(name = "id") String id) throws FileServiceException {
		FileItemEntity f = em.find(FileItemEntity.class, id);
		return f != null && f.isPending();
	}


	@Override
	@WebMethod(operationName = "completePendingFile")
	public void completePendingFile(@WebParam(name = "id") String id, @WebParam(name = "path") Path path) throws FileServiceException {
		LOGGER.log(Level.INFO, "CompletePendingFile  called (" + id + "|" + path.getFileName() +")");
		FileItemEntity f = em.find(FileItemEntity.class, id);
		f.setPath(path);
		em.persist(f);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String postFile(String owner, List<String> receivers, String message, String name, byte[] data) throws FileServiceException {
		LOGGER.log(Level.INFO, "Post File called (byte[])");
		return this.internalPostFile(owner, receivers, message, name, new ByteArrayInputStream(data));
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String postFile(String owner, List<String> receivers, String message, String name, FileData data) throws FileServiceException {
		LOGGER.log(Level.INFO, "Post File called (DataHandler)");
		try {
			return this.internalPostFile(owner, receivers, message, name, data.getData().getInputStream());
		} catch (IOException e) {
			throw new FileServiceException("error during posting file", e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String postFile(String owner, List<String> receivers, String message, String name, InputStream stream) throws FileServiceException {
		LOGGER.log(Level.INFO, "Post File called (InputStream)");
		return this.internalPostFile(owner, receivers, message, name, stream);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private String internalPostFile(String owner, List<String> receivers, String message, String name, InputStream stream) throws FileServiceException {
		try {
			Path streamid = store.put(stream);
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			FileItemEntity file = new FileItemEntity();
			file.setId(id);
			file.setOwner(owner);
			file.setReceivers(receivers);
			file.setMessage(message);
			file.setName(name);
			file.setPath(streamid);
			em.persist(file);
			
			this.notify(owner, receivers, id, message);
			return id;
		} catch ( BinaryStoreServiceException e ) {
			LOGGER.log(Level.SEVERE, "An error occured during storing binary content", e);
			ctx.setRollbackOnly();
			throw new FileServiceException("An error occured during storing binary content", e);
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "unexpected error during posting file", e);
			throw new FileServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public FileItem getFile(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get File called");
		try {
			FileItemEntity item = em.find(FileItemEntity.class, id);
			if ( item == null ) {
				throw new FileServiceException("Unable to get file with id '" + id + "' : file does not exists");
			}
			return item;
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "An error occured during getting file", e);
			throw new FileServiceException(e);
		}
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public InputStream getFileContent(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get File Content called");
		return this.internalGetFileContent(id);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public byte[] getWholeFileContent(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get Whole File Content called");
		InputStream is = this.internalGetFileContent(id);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len = 0;
			while ( (len=is.read(buffer)) != -1) {
			    baos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			throw new FileServiceException("unable to copy stream", e);
		} finally {
			try {
				baos.flush();
				baos.close();
				is.close();
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "error during closing streams", e);
			}
		}
		return baos.toByteArray();
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public DataHandler getFileData(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get File Data called");
		InputStream is = this.internalGetFileContent(id);
		return new DataHandler(new InputStreamDataSource(is));
	}
	
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private InputStream internalGetFileContent(String id) throws FileServiceException {
		try {
			FileItemEntity item = em.find(FileItemEntity.class, id);
			if ( item == null ) {
				throw new FileServiceException("Unable to get file with id '" + id + "' : file does not exists");
			}
			InputStream is = store.get(item.getPath());
			return is;
		} catch ( BinaryStreamNotFoundException e ) {
			LOGGER.log(Level.SEVERE, "No binary content found for this file item !!", e);
			throw new FileServiceException("No binary content found for this file item !!", e);
		} catch ( BinaryStoreServiceException e ) {
			LOGGER.log(Level.SEVERE, "An error occured during reading binary content", e);
			throw new FileServiceException("An error occured during reading binary content", e);
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "unexpected error during getting file", e);
			throw new FileServiceException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteFile(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Delete File called");
		try {
			FileItemEntity item = em.find(FileItemEntity.class, id);
			if ( item == null ) {
				throw new FileServiceException("Unable to delete file with id '" + id + "' : file does not exists");
			}
			em.remove(item);
			try {
				store.delete(item.getPath());
			} catch ( BinaryStreamNotFoundException | BinaryStoreServiceException e ) {
				LOGGER.log(Level.WARNING, "unable to delete binary content, may result in orphean file", e);
			}
		} catch ( Exception e ) {
			LOGGER.log(Level.SEVERE, "unexpected error during deleting file", e);
			ctx.setRollbackOnly();
			throw new FileServiceException(e);
		}
	}
	
	@Override
	@RolesAllowed({"admin", "system"})
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<FileItem> listAllFiles() throws FileServiceException {
		LOGGER.log(Level.INFO, "Listing all files");
		List<FileItemEntity> results = em.createNamedQuery("listAllFiles", FileItemEntity.class).getResultList();
		List<FileItem> items = new ArrayList<FileItem>();
		items.addAll(results);
		return items;
	}

	@Override
	@RolesAllowed({"admin", "system"})
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public FileItemEntity getNextStaleFile() throws FileServiceException {
		LOGGER.log(Level.INFO, "Getting next stale files");
		Date limit = new Date(System.currentTimeMillis() - 60000);
		try {
			FileItemEntity item = em.createNamedQuery("findExpiredFiles", FileItemEntity.class).setParameter("limit", limit,  TemporalType.TIMESTAMP).setMaxResults(1).getSingleResult();
			LOGGER.log(Level.INFO, "next stale file item found: " + item.getId());
			return item;
		} catch ( Exception e ) {
			LOGGER.log(Level.INFO, "no stale file item found: " + e.getMessage());
			return null;
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void notify(String owner, List<String> receivers, String id, String message) throws FileServiceException {
		try {
			javax.jms.Message msg = jmsctx.createMessage();
			msg.setStringProperty("owner", owner);
			StringBuilder receiversBuilder =  new StringBuilder();
			for ( String receiver : receivers ) {
				receiversBuilder.append(receiver).append(",");
			}
			receiversBuilder.deleteCharAt(receiversBuilder.lastIndexOf(","));
			msg.setStringProperty("receivers", receiversBuilder.toString());
			msg.setStringProperty("id", id);
			msg.setStringProperty("message", message);
			jmsctx.createProducer().send(notificationTopic, msg);
			
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "unable to notify", e);
			throw new FileServiceException("unable to notify", e);
		}
	}
	
	class InputStreamDataSource implements DataSource {
		private InputStream inputStream;

	    public InputStreamDataSource(InputStream inputStream) {
	        this.inputStream = inputStream;
	    }

	    @Override
	    public InputStream getInputStream() throws IOException {
	        return inputStream;
	    }

	    @Override
	    public OutputStream getOutputStream() throws IOException {
	        throw new UnsupportedOperationException("Not implemented");
	    }

	    @Override
	    public String getContentType() {
	        return "*/*";
	    }

	    @Override
	    public String getName() {
	        return "InputStreamDataSource";
	    }
	}

}
