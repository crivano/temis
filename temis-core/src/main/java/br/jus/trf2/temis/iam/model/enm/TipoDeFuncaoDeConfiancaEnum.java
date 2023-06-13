package br.jus.trf2.temis.iam.model.enm;

import lombok.AllArgsConstructor;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

@AllArgsConstructor
// @Getter
@Global(singular = "Tipo de Função de Confiança", plural = "Tipos de Função de Confiança", gender = Gender.HE)
public enum TipoDeFuncaoDeConfiancaEnum implements IEnum {
	CHEFIA("Chefia"),
	//
	ACESSORIA("Acessoria"),
	//
	NENHUMA("Nenhuma");

	private final String descr;

	@Override
	public Long getId() {
		return (long) this.ordinal();
	}

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getDescr() {
		return this.descr;
	}
}
