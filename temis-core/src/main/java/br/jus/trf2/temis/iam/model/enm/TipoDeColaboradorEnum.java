package br.jus.trf2.temis.iam.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;

@AllArgsConstructor
// @Getter
@Global(singular = "Tipo de Colaborador", plural = "Tipos de Colaboradores", gender = Gender.HE)
public enum TipoDeColaboradorEnum implements IEnum {
	FUNCIONARIO("Funcionário"),
	//
	TERCERIZADO("Tercerizado"),
	//
	ESTAGIARIO("Estagiário");

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
