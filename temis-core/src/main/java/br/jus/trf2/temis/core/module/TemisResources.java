package br.jus.trf2.temis.core.module;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import lombok.Data;
import lombok.NoArgsConstructor;

@ApplicationScoped
@Data
@NoArgsConstructor
public class TemisResources {

	private List<TemisResource> resources = new ArrayList<>();

	public TemisDownload getResources(TemisApp app, TemisResourceKindEnum kind) throws IOException {
		StringBuilder sb = new StringBuilder();

		if (kind == null)
			throw new FileNotFoundException("file kind unknown");

		for (TemisResource r : resources) {
			if (kind.equals(r.kind)) {
				if (kind == TemisResourceKindEnum.RESOURCE_JS && r.pathName != null)
					sb.append("//\n// RESOURCE: " + r.pathName + "\n//\n\n");
				else if (kind == TemisResourceKindEnum.RESOURCE_JS && r.clazz != null)
					sb.append("//\n// ENUM: " + r.clazz.getSimpleName() + "\n//\n\n");
				else if (kind == TemisResourceKindEnum.MODEL_JSON && r.clazz != null) {
					if (sb.length() == 0)
						sb.append("[");
					else
						sb.append(",");
				}
				sb.append(r.getAsString(app));
				if (kind == TemisResourceKindEnum.RESOURCE_JS)
					sb.append("\n\n");
			}
		}
		if (kind == TemisResourceKindEnum.MODEL_JSON)
			sb.append("]");
		byte[] buf = sb.toString().getBytes(StandardCharsets.UTF_8);

		MessageDigest md;
		String etag = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(buf);
			etag = new String(Base64.getEncoder().encode(md.digest()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		TemisDownload dl = new TemisDownload(buf, kind.contentType, kind.filename, false, etag, true, null);
		return dl;
	}

	public void add(TemisResourceKindEnum kind, Class clazz) {
		resources.add(new TemisResource(kind, clazz));
	}

	public void add(TemisResourceKindEnum kind, String pathname) {
		resources.add(new TemisResource(kind, pathname));
	}

}
