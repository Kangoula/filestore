package org.filestore.ejb.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.DriverManager;
import java.sql.SQLNonTransientConnectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.filestore.ejb.file.entity.FileItemEntity;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FileItemTest {

	private static Logger LOGGER = Logger.getLogger(FileItemTest.class.getName());

    private static EntityManagerFactory emFactory;
    private static EntityManager em;

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
            emFactory = Persistence.createEntityManagerFactory("testPU");
            em = emFactory.createEntityManager();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception during JPA EntityManager instanciation.");
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
    	LOGGER.log(Level.INFO, "Shuting Hibernate JPA layer.");
        if (em != null) {
            em.close();
        }
        if (emFactory != null) {
            emFactory.close();
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
    public void testFileItem() {
        try {
            em.getTransaction().begin();

            FileItemEntity file = new FileItemEntity();
            file.setId("myid");
            file.setName("Tagada");
            file.setLength(10);
            file.setMessage("this is a message");
            file.setOwner("miage");
            file.setType("text/plain");
            file.setReceivers(new ArrayList<String> ());
            
            em.persist(file);
            assertTrue(em.contains(file));
            
            FileItemEntity file2 = em.find(FileItemEntity.class, "myid");
            assertNotNull(file2);
            assertEquals(file, file2);
            
            assertEquals("Tagada", file2.getName());
            file2.setName("A new Name");
            List<String> receivers = new ArrayList<String>();
            receivers.add("user1@test.com");
            receivers.add("user2@test.com");
            file2.setReceivers(receivers);
            em.merge(file2);
            FileItemEntity file3 = em.find(FileItemEntity.class, "myid");
            assertEquals("A new Name", file3.getName());
            
            List<FileItemEntity> items = em.createNamedQuery("listAllFiles", FileItemEntity.class).getResultList();
            assertEquals(1, items.size());
            
            FileItemEntity file4 = new FileItemEntity();
            file4.setId("myid2");
            file4.setName("Tagada2");
            file4.setLength(20);
            file4.setMessage("this is another message");
            file4.setOwner("miage");
            file4.setType("text/plain");
            List<String> receivers4 = new ArrayList<String>();
            receivers4.add("user2@test.com");
            receivers4.add("user3@test.com");
            file4.setReceivers(receivers4);
            em.persist(file4);
            
            items = em.createNamedQuery("listAllFiles", FileItemEntity.class).getResultList();
            assertEquals(2, items.size());
            
            FileItemEntity file5 = em.find(FileItemEntity.class, "myid2");
            assertEquals(2, file5.getReceivers().size());
            for ( String receiver : file5.getReceivers() ) {
            	LOGGER.log(Level.INFO, "receiver: " + receiver);
            }

            em.getTransaction().commit();

        } catch (Exception e) {
            em.getTransaction().rollback();
            LOGGER.log(Level.SEVERE, "error during testing file item", e);
            fail("Exception during testPersistence");
        }
    }

}
