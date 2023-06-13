package br.jus.trf2.temis.iam.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.iam.model.enm.UfEnum;
import lombok.Data;

@Data
@Global(singular = "RG", plural = "RGs", gender = Gender.HE)
public class Rg {
	@Edit(caption = "Número", colM = 6, colL = 3)
	private String numero;

	@Edit(caption = "Órgão Emissor", colM = 6, colL = 3)
	private String orgao;

	@Edit(caption = "UF", colM = 6, colL = 3)
	@Enumerated(EnumType.STRING)
	private UfEnum uf;

	@Edit(caption = "Data de Emissão", colM = 6, colL = 3)
	private LocalDate data;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (numero != null) {
			sb.append(numero);
		}
		if (orgao != null) {
			sb.append(" ");
			sb.append(orgao);
		}
		if (uf != null) {
			sb.append("/");
			sb.append(uf.name());
		}
		if (data != null) {
			sb.append(" emitido em ");
			sb.append(data);
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}
}
