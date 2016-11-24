package org.filestore.api;

import org.jboss.resource.adapter.jdbc.remote.SerializableInputStream;

import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType
public class FileData implements Serializable {

	private SerializableInputStream data;

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
	public SerializableInputStream getData() {
			return data;

	}

	public void setData(SerializableInputStream data) {
		this.data = data;
	}

}
