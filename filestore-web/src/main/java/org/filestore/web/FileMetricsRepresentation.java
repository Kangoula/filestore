package org.filestore.web;

public class FileMetricsRepresentation {

	private long downloads;
	private long uploads;
	private long uptime;

	public FileMetricsRepresentation(long downloads, long uploads, long uptime) {
		super();
		this.downloads = downloads;
		this.uploads = uploads;
		this.uptime = uptime;
	}

	public long getDownloads() {
		return downloads;
	}

	public void setDownloads(long downloads) {
		this.downloads = downloads;
	}

	public long getUploads() {
		return uploads;
	}

	public void setUploads(long uploads) {
		this.uploads = uploads;
	}

	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

}
