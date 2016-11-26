package org.filestore.api;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType
public class FileData implements Serializable {

	private DataHandler data;

	private String name;

	private long size;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
