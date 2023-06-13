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
@Global(singular = "PDF", plural = "PDFs", gender = Gender.HE)
public class ArquivoAssinaturaDigital extends Arquivo {
	private static final String APPLICATION_PKCS7_SIGNATURE = "application/pkcs7-signature";

//	public ArquivoAssinaturaDigital(Ref<Empresa> refEmpresa, byte[] bytes, Date dhAssinatura) throws IOException {
//		super(refEmpresa, APPLICATION_PKCS7_SIGNATURE, bytes.length);
//
//		setTime(dhAssinatura);
//
//		Dao dao = new Dao();
//		dao.save(this);
//
//		setContent(bytes);
//		dao.save(this);
//	}

	@Override
	public String extension() {
		return ".cms";
	}

	public static boolean tipoCompativel(String contentType) {
		return contentType.startsWith(APPLICATION_PKCS7_SIGNATURE);
	}

}
