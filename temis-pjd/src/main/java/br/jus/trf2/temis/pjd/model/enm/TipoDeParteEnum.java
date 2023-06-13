package br.jus.trf2.temis.pjd.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Global(singular = "Tipo de Parte", plural = "Tipos de Parte", gender = Gender.HE)
public enum TipoDeParteEnum implements IEnum {
	ATIVO_PARTE("Polo Ativo - Parte", TipoDePoloEnum.ATIVO),
	//
	ATIVO_PROCURADOR("Polo Ativo - Procurador", TipoDePoloEnum.ATIVO),
	//
	ATIVO_TERCEIRO_VINCULADO("Polo Ativo - Terceiro Vinculado", TipoDePoloEnum.ATIVO),
	//
	PASSIVO_PARTE("Polo Passivo - Parte", TipoDePoloEnum.PASSIVO),
	//
	PASSIVO_PROCURADOR("Polo Passivo - Procurador", TipoDePoloEnum.PASSIVO),
	//
	PASSIVO_TERCEIRO_VINCULADO("Polo Passivo - Terceiro Vinculado", TipoDePoloEnum.PASSIVO),
	//
	OUTROS_PARTICIPANTE("Outros - Parte", TipoDePoloEnum.OUTROS_PARTICIPANTES),
	//
	OUTROS_PROCURADOR("Outros - Procurador", TipoDePoloEnum.OUTROS_PARTICIPANTES),
	//
	OUTROS_TERCEIRO_VINCULADO("Outros - Terceiro Vinculado", TipoDePoloEnum.OUTROS_PARTICIPANTES);

	private final String nome;
	private final TipoDePoloEnum tipoDePolo;

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
