package br.jus.trf2.temis.core.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.observer.download.Download;

public class CachedDownload implements Download, Serializable {
	public byte[] bytes;
	public String contentType;
	public String fileName;
	public boolean doDownload;
	public long size;
	public String etag;
	public boolean cachePublic;
	public Long cacheExpires;

	public CachedDownload(byte[] bytes, String contentType, String fileName) {
		this(bytes, contentType, fileName, false, null, false, null);
	}

	public CachedDownload(byte[] bytes, String contentType, String fileName, boolean doDownload, String etag,
			boolean cachePublic, Long cacheExpires) {
		this.bytes = bytes;
		this.contentType = contentType;
		this.fileName = fileName;
		this.doDownload = doDownload;
		this.etag = etag;
		this.cachePublic = cachePublic;
		this.cacheExpires = cacheExpires;
	}

	public void write(HttpServletResponse response) throws IOException {
		writeDetails(response);

		OutputStream out = response.getOutputStream();
		out.write(bytes);
	}

	void writeDetails(HttpServletResponse response) {
		if (contentType != null) {
			String contentDisposition = String.format("%s; filename=%s", doDownload ? "attachment" : "inline",
					fileName);
			response.setHeader("Content-disposition", contentDisposition);
			response.setHeader("Content-type", contentType);
		}
		response.setHeader("Content-Length", Long.toString(bytes.length));
		if (etag != null) {
			if (cacheExpires == null) {
				response.setHeader("Cache-Control", "must-revalidate, " + (this.cachePublic ? "public" : "private"));
				response.setDateHeader("Expires", 0);
			} else {
				response.setHeader("Cache-Control", (this.cachePublic ? "public" : "private"));
				response.setDateHeader("Expires", (new Date().getTime()) + cacheExpires);
			}
			response.setHeader("ETag", etag);
		}
	}

}
