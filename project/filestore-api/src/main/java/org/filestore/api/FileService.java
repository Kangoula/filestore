package org.filestore.api;

import org.jboss.resource.adapter.jdbc.remote.SerializableInputStream;

import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.util.List;

@Remote
@WebService
@SOAPBinding(style=Style.RPC)
public interface FileService {
	
	@WebMethod(operationName="postfile")
	public String postFile(@WebParam(name="owner") String owner, @WebParam(name="receivers") List<String> receivers, @WebParam(name="message") String message, @WebParam(name="filename") String name, @WebParam(name="filecontent") byte[] data) throws FileServiceException;
	
	@WebMethod(operationName="postfile2")
	public String postFile(@WebParam(name="owner") String owner, @WebParam(name="receivers") List<String> receivers, @WebParam(name="message") String message, @WebParam(name="filename") String name, @WebParam(name="filedata") FileData data) throws FileServiceException;
	
	@WebMethod(operationName="getfile")
	@WebResult(name="fileitem")
	public FileItem getFile(@WebParam(name="id") String id) throws FileServiceException;
	
	@WebMethod(operationName="getfilecontent")
	@WebResult(name="filecontent")
	public String getWholeFileContent(@WebParam(name="id") String id) throws FileServiceException;
	
	@WebMethod(operationName="getfilecontent2")
	@WebResult(name="filecontent")
	public String getFileData(@WebParam(name="id") String id) throws FileServiceException;

}
