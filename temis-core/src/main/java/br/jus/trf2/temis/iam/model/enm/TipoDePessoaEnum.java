package br.jus.trf2.temis.iam.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

@Global(singular = "Tipo de Pessoa", plural = "Tipos de Pessoa", gender = Gender.HE)
public enum TipoDePessoaEnum implements IEnum {
	PESSOA_FISICA(1, "Pessoa Física"),
	//
	PESSOA_JURIDICA(2, "Pessoa Jurídica"),
	//
	ENTIDADE(3, "Entidade");

	private final long id;
	private final String descr;

	TipoDePessoaEnum(long id, String descr) {
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
