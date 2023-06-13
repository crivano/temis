package br.jus.trf2.temis.core;

import javax.persistence.Entity;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Global(singular = "JSON", plural = "JSONs", gender = Gender.HE)
public class ArquivoJson extends Arquivo {
	private static final String APPLICATION_JSON = "application/json";

	@Override
	public String extension() {
		return ".json";
	}

	public static boolean tipoCompativel(String contentType) {
		return contentType.startsWith(APPLICATION_JSON);
	}

}
