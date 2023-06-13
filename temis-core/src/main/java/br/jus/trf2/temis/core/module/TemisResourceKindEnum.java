package br.jus.trf2.temis.core.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemisResourceKindEnum {
	ASSETS_CSS("assets.css", "text/css;charset=utf-8"),
	//
	ASSETS_JS("assets.js", "application/javascript;charset=utf-8"),
	//
	RESOURCE_CSS("resources.css", "text/css;charset=utf-8"),
	//
	RESOURCE_JS("resources.js", "application/javascript;charset=utf-8"),
	//
	MODEL_JSON("model.json", "application/javascript;charset=utf-8");

	String filename;
	String contentType;

	public static TemisResourceKindEnum getByFilename(String filename) {
		if (filename == null)
			return null;
		for (TemisResourceKindEnum mt : TemisResourceKindEnum.values())
			if (mt.filename.equalsIgnoreCase(filename))
				return mt;
		return null;
	}
}
