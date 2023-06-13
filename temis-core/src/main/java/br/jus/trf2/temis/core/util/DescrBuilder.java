package br.jus.trf2.temis.core.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor(staticName = "builder")
public class DescrBuilder {
	StringBuilder sb = new StringBuilder();
	String separator = ", ";

	public DescrBuilder add(String prefix, String content) {
		return add(separator, prefix, content, "");
	}

	public DescrBuilder add(String separator, String prefix, String content, String suffix) {
		if (content == null || content.trim().length() == 0)
			return this;
		if (sb.length() > 0)
			sb.append(separator);
		sb.append(prefix);
		sb.append(content);
		sb.append(suffix);
		return this;
	}

	public String build() {
		return sb.toString();
	}
}
