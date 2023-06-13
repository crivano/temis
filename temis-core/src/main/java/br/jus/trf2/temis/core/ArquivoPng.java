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
@Global(singular = "Imagem", plural = "Imagens", gender = Gender.SHE)
public class ArquivoPng extends ArquivoImagem {
	public static final String IMAGE_PNG = "image/png";

	@Override
	public String extension() {
		return ".png";
	}

	public static boolean tipoCompativel(String contentType) {
		return contentType.startsWith(IMAGE_PNG);
	}
}
