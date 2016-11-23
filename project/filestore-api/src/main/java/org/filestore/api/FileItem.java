package org.filestore.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name="file-item")
public abstract class FileItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlAttribute(name="id")
	public abstract String getId();

	@XmlElement(name="name")
	public abstract String getName();
	
	@XmlElement(name="type")
	public abstract String getType();
	
	public abstract long getLength();

	public abstract long getNbdownloads();
	
	@XmlElement(name="owner")
	public abstract String getOwner();

	@XmlTransient
	public abstract List<String> getReceivers();

	public abstract String getMessage();

	public abstract Date getCreation();

	public abstract Date getLastdownload();

}
