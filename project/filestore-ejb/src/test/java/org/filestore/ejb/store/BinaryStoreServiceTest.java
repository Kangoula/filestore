package org.filestore.ejb.store;

import org.junit.BeforeClass;

public class BinaryStoreServiceTest {
	
	private static S3StoreService service;
	
	@BeforeClass
	public static void init() {
		service = new S3StoreServiceBean();
		((S3StoreServiceBean)service).init();
	}
	

	/*@Test
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
	}*/

}
