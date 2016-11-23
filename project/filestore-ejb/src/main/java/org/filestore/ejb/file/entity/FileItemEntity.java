package org.filestore.ejb.file.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.filestore.api.FileItem;

@Entity
@NamedQueries({ 
	@NamedQuery(name = "listAllFiles", query = "SELECT fi FROM FileItemEntity fi"),
	@NamedQuery(name = "findExpiredFiles", query = "SELECT fi FROM FileItemEntity fi WHERE fi.lastdownload < :limit ORDER BY fi.lastdownload DESC") 
})
@Table(indexes={@Index(name="lastdownloadindex", columnList="lastdownload")})
public class FileItemEntity extends FileItem {

	private static final long serialVersionUID = -1869502504816752908L;

	@Id
	private String id;
	private String name;
	private String type;
	private long length;
	private long nbdownloads;
	private String owner;
	@ElementCollection
	private List<String> receivers;
	@Column(length = 2000)
	private String message;
	@Temporal(TemporalType.TIMESTAMP)
	private Date creation;
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastdownload;
	private String stream;

	public FileItemEntity() {
		nbdownloads = 0;
		creation = new Date();
		lastdownload = creation;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public long getNbdownloads() {
		return nbdownloads;
	}

	public void setNbdownloads(long nbdownloads) {
		this.nbdownloads = nbdownloads;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public List<String> getReceivers() {
		return receivers;
	}

	public void setReceivers(List<String> receivers) {
		this.receivers = receivers;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getCreation() {
		return creation;
	}

	public void setCreation(Date creation) {
		this.creation = creation;
	}

	public Date getLastdownload() {
		return lastdownload;
	}

	public void setLastdownload(Date lastdownload) {
		this.lastdownload = lastdownload;
	}

	public String getStream() {
		return stream;
	}

	public void setStream(String stream) {
		this.stream = stream;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creation == null) ? 0 : creation.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastdownload == null) ? 0 : lastdownload.hashCode());
		result = prime * result + (int) (length ^ (length >>> 32));
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (nbdownloads ^ (nbdownloads >>> 32));
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((receivers == null) ? 0 : receivers.hashCode());
		result = prime * result + ((stream == null) ? 0 : stream.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileItemEntity other = (FileItemEntity) obj;
		if (creation == null) {
			if (other.creation != null)
				return false;
		} else if (!creation.equals(other.creation))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lastdownload == null) {
			if (other.lastdownload != null)
				return false;
		} else if (!lastdownload.equals(other.lastdownload))
			return false;
		if (length != other.length)
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nbdownloads != other.nbdownloads)
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (receivers == null) {
			if (other.receivers != null)
				return false;
		} else if (!receivers.equals(other.receivers))
			return false;
		if (stream == null) {
			if (other.stream != null)
				return false;
		} else if (!stream.equals(other.stream))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
