package org.filestore.ejb.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;

public class BinaryStoreServiceTest {
	
	private static BinaryStoreService service;
	
	@BeforeClass
	public static void init() {
		service = new BinaryStoreServiceBean();
		((BinaryStoreServiceBean)service).init();
	}
	
	@Test
	public void testPut() throws BinaryStoreServiceException {
		String wrongkey = "blabla";
		String key = service.put(new ByteArrayInputStream("This is a super stream".getBytes()));
			
		assertTrue(service.exists(key));
		assertFalse(service.exists(wrongkey));
	}
	
	@Test (expected=BinaryStreamNotFoundException.class)
	public void testGetWrongKey() throws BinaryStoreServiceException, BinaryStreamNotFoundException {
		String wrongkey = "akeythatdoesnotexists";
		service.get(wrongkey);
	}
	
	@Test
	public void testGet() throws BinaryStoreServiceException, BinaryStreamNotFoundException {
		String content = "This is a magical content"; 
		InputStream original = new ByteArrayInputStream(content.getBytes());
		String key = service.put(original);
		try ( InputStream in = service.get(key) ) {
			int b = 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ( (b = in.read()) != -1 ) {
				baos.write(b);
			}
			String retreive = new String(baos.toByteArray()); 
			assertEquals(retreive, content);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
