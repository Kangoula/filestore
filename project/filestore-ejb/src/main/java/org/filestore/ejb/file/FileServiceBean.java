package org.filestore.ejb.file;

import org.filestore.api.*;
import org.filestore.ejb.file.entity.FileItemEntity;
import org.filestore.ejb.file.metrics.FileServiceMetricsBean;
import org.filestore.ejb.store.BinaryStoreServiceException;
import org.filestore.ejb.store.BinaryStreamNotFoundException;
import org.filestore.ejb.store.S3StoreServiceBean;
import org.jboss.ejb3.annotation.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.transaction.UserTransaction;
import javax.xml.ws.soap.MTOM;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless(name = "fileservice")
@Interceptors ({FileServiceMetricsBean.class})
@SecurityDomain("filestore")
@PermitAll
@MTOM(enabled=true)
@WebService(endpointInterface = "org.filestore.api.FileServiceS3")
@HandlerChain(file="/handler-chain.xml")
@TransactionManagement(value=TransactionManagementType.BEAN)
public class FileServiceBean implements  FileServiceLocal, FileServiceAdmin, FileServiceS3 {
	
	private static final Logger LOGGER = Logger.getLogger(FileServiceBean.class.getName());
	
	@PersistenceContext(unitName="filestore-pu")
	protected EntityManager em;
	@Resource
	protected SessionContext ctx;
	@EJB
	protected S3StoreServiceBean store;
	@Resource(mappedName = "java:jboss/exported/jms/topic/Notification")
	private Topic notificationTopic;
	@Inject 
	private JMSContext jmsctx;
	@Resource
	private UserTransaction ut;
	
	/*@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String postFile(String owner, List<String> receivers, String message, String name, byte[] data) throws FileServiceException {
		LOGGER.log(Level.INFO, "Post File called (byte[])");
		return this.internalPostFile(owner, receivers, message, name, new ByteArrayInputStream(data));
	}*/

	/*@Override
	@WebMethod(operationName = "postfile")
	public String postFile(@WebParam(name = "owner") String owner, @WebParam(name = "receivers") List<String> receivers, @WebParam(name = "message") String message, @WebParam(name = "filename") String name, @WebParam(name = "filecontent") byte[] data) throws FileServiceException {
		return null;
	}*/

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String postFile(String owner, List<String> receivers, String message, String name, FileData data) throws FileServiceException {
		LOGGER.log(Level.INFO, "Post File called (DataHandler)");

		return this.internalPostFile(owner, receivers, message, name, data);
	}
	
/*	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String postFile(String owner, List<String> receivers, String message, String name, FileData stream) throws FileServiceException {
		LOGGER.log(Level.INFO, "Post File called (InputStream)");
		return this.internalPostFile(owner, receivers, message, name, stream);
	}*/


	private String internalPostFile(String owner, List<String> receivers, String message, String name, FileData stream) throws FileServiceException {

		try {
			String streamid = store.put(stream);
			ut.begin();
			String id = UUID.randomUUID().toString().replaceAll("-", "");
			FileItemEntity file = new FileItemEntity();
			file.setId(id);
			file.setOwner(owner);
			file.setReceivers(receivers);
			file.setMessage(message);
			file.setName(name);
			file.setStream(streamid);
			em.persist(file);
			ut.commit();
			
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
	public String getFileContent(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get File Content called");
		return this.internalGetFileContent(id);
	}

	@Override
	public String postFile(String owner, List<String> receivers, String message, String name, InputStream stream) throws FileServiceException {
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getWholeFileContent(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get Whole File Content called");
		String url = this.internalGetFileContent(id);

		return url;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String getFileData(String id) throws FileServiceException {
		LOGGER.log(Level.INFO, "Get File Data called");
		String url = this.internalGetFileContent(id);
		return url;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private String internalGetFileContent(String id) throws FileServiceException {
		try {
			FileItemEntity item = em.find(FileItemEntity.class, id);
			if ( item == null ) {
				throw new FileServiceException("Unable to get file with id '" + id + "' : file does not exists");
			}
			String url = store.get(item.getStream());
			return url;
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
				store.delete(item.getStream());
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


}
