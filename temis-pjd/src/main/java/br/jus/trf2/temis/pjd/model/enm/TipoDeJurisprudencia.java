package br.jus.trf2.temis.pjd.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Tipo de exibição de marcador", plural = "Tipos de exibição de marcador", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum TipoDeJurisprudencia implements IEnum {
	SENTENCA("Sentença"),
	//
	DECISAO_MONOCRATICA("Decisão Monocrática"),
	//
	ACORDAO("Acórdão"),
	//
	ACORDAO_RECURSOS_REPETITIVOS("Acórdão proferido sob a sistemática dos Recursos Repetitivos"),
	//
	ACORDAO_REPERCUSSAO_GERAL("Acórdão proferido sob a sistemática dos Repercussão Geral");

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
