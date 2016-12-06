package org.filestore.api;

import java.nio.file.Path;
import java.util.List;

import javax.activation.DataHandler;
import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

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
	public byte[] getWholeFileContent(@WebParam(name="id") String id) throws FileServiceException;


	@WebMethod(operationName="preparePostFile")
	public String preparePostFile(
			@WebParam(name="owner") String owner,
			@WebParam(name="receivers") List<String> receivers,
			@WebParam(name="message") String message
	) throws FileServiceException;


	@WebMethod(operationName = "hasPendingFile")
	public boolean hasPendingFile(
			@WebParam(name="id") String id
	) throws FileServiceException;

	@WebMethod(operationName = "completePendingFile")
	public void completePendingFile(
			@WebParam(name="id") String id,
			@WebParam(name="path") String filePath
	) throws FileServiceException;


	@WebMethod(operationName="getfilecontent2")
	@WebResult(name="filecontent")
	public DataHandler getFileData(@WebParam(name="id") String id) throws FileServiceException;

}
