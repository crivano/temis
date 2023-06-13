package br.jus.trf2.temis.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Date;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.util.NoSerialization;
import br.jus.trf2.temis.core.util.Utils;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 50)
@Data
@FieldNameConstants
@Global(singular = "Arquivo", plural = "Arquivos", gender = Gender.HE)
public class Arquivo {

	@IgnoreForSimilarity
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Edit
	Long id;

	private Date begin;
	private String filename;

	@NotNull
	private String contentType;

	@Lob
	@NoSerialization
	byte[] bytes;

	private long length;

	private byte[] sha1;
	private byte[] sha256;

	private Date time;

	public static Arquivo of(String contentType, String filename, byte[] bytes) {
		Arquivo arq = null;
		if (ArquivoPng.tipoCompativel(contentType))
			arq = new ArquivoPng();
		else if (ArquivoJpeg.tipoCompativel(contentType))
			arq = new ArquivoJpeg();
		else if (ArquivoHtml.tipoCompativel(contentType))
			arq = new ArquivoHtml();
		else if (ArquivoPdf.tipoCompativel(contentType))
			arq = new ArquivoPdf();
		else if (ArquivoJson.tipoCompativel(contentType))
			arq = new ArquivoJson();
		else
			throw new RuntimeException("Tipo de conteúdo desconhecido: " + contentType);
		arq.setContentType(contentType);
		arq.setLength(bytes.length);
		arq.setBegin(Utils.newDate());
		arq.setBytes(bytes);
		arq.updateHash();
		return arq;
	}

	public void updateHash() {
		int bufferSize = 64 * 1024;

		try (InputStream in = toInputStream()) {
			MessageDigest mdsha1 = MessageDigest.getInstance("SHA-1");
			MessageDigest mdsha256 = MessageDigest.getInstance("SHA-256");

			byte[] buffer = new byte[bufferSize];
			int sizeRead = -1;
			while ((sizeRead = in.read(buffer)) != -1) {
				mdsha1.update(buffer, 0, sizeRead);
				mdsha256.update(buffer, 0, sizeRead);
			}

			sha1 = mdsha1.digest();
			sha256 = mdsha256.digest();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private InputStream toInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	public String extension() {
		return null;
	}

}
