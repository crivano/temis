package br.jus.trf2.temis.core;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Tipo de Visibilidade de Evento", plural = "Tipos de Visibilidade de Eventos", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum VisibilidadeDeEventoEnum implements IEnum {
	EXIBIR_NUNCA("Nunca exibir"),
	//
	EXIBIR_SE_AUDITANDO("Exibir só quando está sendo auditado"),
	//
	EXIBIR_SEMPRE("Sempre exibir");

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
