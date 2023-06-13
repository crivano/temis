package br.jus.trf2.temis.core.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Tipo de exibição de marcador", plural = "Tipos de exibição de marcador", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum TipoDePessoaJuridicaEnum implements IEnum {
	MEI("Microempreendedor Individual"),
	//
	EI("Empresário Individual"),
	//
	EIRELI("Empresa Individual de Responsabilidade Limitada"),
	//
	LTDA_ME("Microempresa"),
	//
	LTDA_EPP("Empresa de Pequeno Porte"),
	//
	LTDA_SLU("Sociedade Limitada Unipessoal"),
	//
	LTDA("Sociedade Empresária Limitada"),
	//
	SA("Sociedade Anônima");

	private final String nome;

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
		return this.nome;
	}
}
