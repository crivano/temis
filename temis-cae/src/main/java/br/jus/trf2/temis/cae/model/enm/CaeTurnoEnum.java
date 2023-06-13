package br.jus.trf2.temis.cae.model.enm;

import com.crivano.jbiz.IEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CaeTurnoEnum implements IEnum {
	MANHA("Manhã"),
	//
	MANHA_E_TARDE("Manhã e Tarde"),
	//
	TARDE("Tarde"),
	//
	TARDE_E_NOITE("Tarde e Noite"),
	//
	NOITE("Noite");

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
