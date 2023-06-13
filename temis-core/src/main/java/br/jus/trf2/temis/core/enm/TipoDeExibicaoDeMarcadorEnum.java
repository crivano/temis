package br.jus.trf2.temis.core.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Tipo de exibição de marcador", plural = "Tipos de exibição de marcador", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum TipoDeExibicaoDeMarcadorEnum implements IEnum {
	MARCADOS("Somente para pessoa ou unidade marcada"),
	//
	GERAL("Visível para todos");

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
