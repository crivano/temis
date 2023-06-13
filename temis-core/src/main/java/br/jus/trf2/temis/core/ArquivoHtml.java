package br.jus.trf2.temis.core;

import javax.persistence.Entity;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Global(singular = "HTML", plural = "HTMLs", gender = Gender.HE)
public class ArquivoHtml extends Arquivo {
	private static final String TEXT_HTML = "text/html";

	@Override
	public String extension() {
		return ".html";
	}

	public static boolean tipoCompativel(String contentType) {
		return contentType.startsWith(TEXT_HTML);
	}

}
