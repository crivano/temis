package br.jus.trf2.temis.iam.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

@Global(singular = "Tipo de Papel", plural = "Tipos de Papel", gender = Gender.HE)
public enum TipoPapelEnum implements IEnum {
	ESTRUTURAL(1, "Estrutural"),
	//
	FUNCIONAL(2, "Funcional"),
	//
	SUBSTITUTIVO(3, "Substitutivo");

	private final long id;
	private final String descr;

	TipoPapelEnum(long id, String descr) {
		this.id = id;
		this.descr = descr;
	}

	@Override
	public Long getId() {
		return this.id;
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
