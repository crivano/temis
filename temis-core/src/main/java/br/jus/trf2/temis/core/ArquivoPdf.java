package br.jus.trf2.temis.core;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

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
@Global(singular = "PDF", plural = "PDFs", gender = Gender.HE)
public class ArquivoPdf extends Arquivo {
	public static final String APPLICATION_PDF = "application/pdf";

	// Precisa ser inicializado no momento do upload do arquivo
	private int paginas;

	@Override
	public void updateHash() {
		super.updateHash();
//		PdfReader reader = new PdfReader(toInputStream());
//		this.paginas = reader.getNumberOfPages();
	}

	public String getText() throws Exception {
//		try (InputStream input = toInputStream()) {
//			StringBuilder sb = new StringBuilder();
//			PdfReader reader = new PdfReader(input);
//			int pages = reader.getNumberOfPages();
//			for (int i = 1; i <= pages; i++) {
//				sb.append(PdfTextExtractor.getTextFromPage(reader, i));
//				sb.append(" ");
//			}
//			String s = sb.toString();
//			if (s == null || s.trim().length() == 0)
//				return null;
//			return s;
//		}
		return null;
	}

	@Override
	public String extension() {
		return ".pdf";
	}

	public static boolean tipoCompativel(String contentType) {
		return contentType.startsWith(APPLICATION_PDF);
	}
}
