package org.filestore.api;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlType
public class FileData {

	private DataHandler data;

	private long size;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@XmlMimeType("application/octet-stream")
	public DataHandler getData() {
		return data;
	}

	public void setData(DataHandler data) {
		this.data = data;
	}

}
