package org.filestore.ejb.file;

import org.filestore.api.*;
import org.filestore.ejb.store.BinaryStoreServiceException;
import org.filestore.ejb.store.BinaryStreamNotFoundException;
import org.filestore.ejb.store.S3StoreServiceBean;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.RollbackException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class FileServiceTest {
	
	private static Logger LOGGER = Logger.getLogger(FileServiceTest.class.getName());
	
	private static EntityManagerFactory factory;
    private static EntityManager em;
    private static FileService service;
    private static FileServiceLocal localService;
    private static FileServiceAdmin adminService;
    private static S3StoreServiceBean store;
    private static Mockery context = new Mockery();

	@BeforeClass
    public static void setUp() throws Exception {
        try {
        	LOGGER.log(Level.INFO, "Starting memory database for unit tests");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-jpa;create=true").close();
        } catch (Exception e) {
        	LOGGER.log(Level.SEVERE, "unable to start database", e);
            fail("Exception during database startup.");
        }
        try {
        	LOGGER.log(Level.INFO, "Building Hibernate EntityManager for unit tests");
            factory = Persistence.createEntityManagerFactory("testPU");
            em = factory.createEntityManager();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception during JPA EntityManager instanciation.");
        }
        LOGGER.log(Level.INFO, "Building FileService");
        service = new FileServiceBean();
        ((FileServiceBean)service).em = em;
        localService = (FileServiceLocal) service;
        adminService = (FileServiceAdmin) service;
        
        store = context.mock(S3StoreServiceBean.class);
        ((FileServiceBean)service).store = store;
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	LOGGER.log(Level.INFO, "Shuting Hibernate JPA layer.");
        if (em != null) {
            em.close();
        }
        if (factory != null) {
        	factory.close();
        }
        LOGGER.log(Level.INFO, "Stopping memory database.");
        try {
            DriverManager.getConnection("jdbc:derby:memory:unit-testing-jpa;shutdown=true").close();
        } catch (SQLNonTransientConnectionException ex) {
            if (ex.getErrorCode() != 45000) {
                throw ex;
            }
        }
    }
    
    @Test
    public void testPostAndDeleteFile() throws FileServiceException {
    	String sample = "This is a sample file content"; 
		InputStream content = new ByteArrayInputStream(sample.getBytes());
    	try {
	    	em.getTransaction().begin();
	    	
	    	context.checking(new Expectations() {{
	            oneOf (store).put(with(any(InputStream.class)));
	            oneOf (store).delete(with(any(String.class)));
	        }});
	    	
	    	List<String> receivers = new ArrayList<String> ();
	    	receivers.add("sheldon@test.com");
	    	receivers.add("rajesh@test.com");
	    	receivers.add("penny@test.com");
	    	String key = localService.postFile("jayblanc@gmail.com", receivers, "Bazinga", "The.Big.Bang.Theory.S06E01.mkv", content);
	    	assertNotNull(key);
	    	
	    	FileItem item = service.getFile(key);
	    	assertEquals("jayblanc@gmail.com", item.getOwner());
	    	assertEquals("Bazinga", item.getMessage());
	    	assertEquals("The.Big.Bang.Theory.S06E01.mkv", item.getName());
	    	
	    	adminService.deleteFile(key);
	    	try {
	    		item = service.getFile(key);
	    		fail("The file should not exists anymore !!");
	    	} catch ( FileServiceException e ) {
	    		//
	    	}
	    	
	    	context.assertIsSatisfied();
	    	
	    	em.getTransaction().commit();
    	}  catch (IllegalStateException | RollbackException | BinaryStoreServiceException | BinaryStreamNotFoundException e) {
            em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "error during testing file service", e);
            fail("Exception during testing file service");
        }
    }

}
