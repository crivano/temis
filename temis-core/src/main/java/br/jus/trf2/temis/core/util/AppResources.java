package br.jus.trf2.temis.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;

import br.jus.trf2.temis.core.module.IResources;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@ApplicationScoped
public class AppResources {
	private static AppResources instance;
	private final Map<String, IResources> map = new HashMap<>();
	private final Pattern p = Pattern.compile(
			"^(?<module>[a-z]*)\\/(?:(?<path>[a-z0-9_\\-\\/]+)\\/)?(?<filename>[a-zA-Z0-9_\\-\\.]+?(?:\\.(?<extension>[a-z0-9]+))?)$");

	@Getter
	@AllArgsConstructor
	public static enum KindEnum {
		ASSETS_CSS("assets.css", "text/css;charset=utf-8"),
		//
		ASSETS_JS("assets.js", "application/javascript;charset=utf-8"),
		//
		RESOURCE_CSS("resources.css", "text/css;charset=utf-8"),
		//
		RESOURCE_JS("resources.js", "application/javascript;charset=utf-8");

		String filename;
		String contentType;

		public static KindEnum getByFilename(String filename) {
			if (filename == null)
				return null;
			for (KindEnum mt : KindEnum.values())
				if (mt.filename.equalsIgnoreCase(filename))
					return mt;
			return null;
		}
	}

	@Data
	@AllArgsConstructor
	public static class Resource {
		public KindEnum kind;
		public String pathName;
	}

	private List<Resource> resources = new ArrayList<>();

	public static AppResources getInstance() {
		return instance;
	}

	public void addModule(String string, IResources resources) {
		map.put(string, resources);
	}

	public String getResourceAsString(String pathName) throws IOException {
		CachedDownload xisd = getResource(pathName);
		if (xisd == null)
			return null;
		String s = new String(xisd.bytes, StandardCharsets.UTF_8);
		return s;
	}

	public CachedDownload getResource(String pathName) throws IOException {
		Matcher m = p.matcher(pathName);
		if (!m.matches())
			throw new IOException("file not found: " + pathName);

		String module = m.group("module");
		String path = m.group("path");
		String filename = m.group("filename");
		String extension = m.group("extension");
		MimeTypeEnum mimeType = MimeTypeEnum.getByExtension(extension);
		String contentType = mimeType.contentType;

		InputStream is = null;

		String root = System.getenv("XRP_DEVELOPMENT_ROOT");
		if (root != null) {
			String pathname = root + "/xrp-" + module + "/src/main/resources/webapp/" + (path != null ? path + "/" : "")
					+ filename;
			File initialFile = new File(pathname);
			try {
				is = new FileInputStream(initialFile);
			} catch (Exception e) {
				return null;
				// throw new RuntimeException("can't find file: " + pathname);
			}
		} else {
			IResources res = map.get(module);
			String pathname = "/resources/" + (path != null ? path + "/" : "") + filename;
			is = res.getResourceAsStream(pathname);
			if (is == null)
				return null;
			// throw new RuntimeException("can't find resource: " + pathname);
		}

		byte[] bytes = Utils.convertStreamToByteArray(is, 32000);
		MessageDigest md;
		String etag = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			etag = Base64.getEncoder().encodeToString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		CachedDownload dl = new CachedDownload(bytes, contentType, filename, false, etag, true, null);
		return dl;

	}

	public CachedDownload getResources(KindEnum kind) throws IOException {
		StringBuilder sb = new StringBuilder();

		for (Resource r : resources) {
			if (kind.equals(r.kind)) {
				sb.append(getResourceAsString(r.pathName));
				sb.append("\n\n");
			}
		}
		byte[] buf = sb.toString().getBytes(StandardCharsets.UTF_8);

		MessageDigest md;
		String etag = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(buf);
			etag = Base64.getEncoder().encodeToString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		CachedDownload dl = new CachedDownload(buf, kind.contentType, kind.filename, false, etag, true, null);
		return dl;
	}

	public void addResource(KindEnum kind, String pathname) {
		resources.add(new Resource(kind, pathname));
	}

	public Set<String> getModules() {
		return map.keySet();
	}

}
