package br.jus.trf2.temis.pjd.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Tipo de exibição de marcador", plural = "Tipos de exibição de marcador", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum TipoDeNormaEnum implements IEnum {
	CONSTITUICAO("Constituição"),
	//
	EMENDA_CONSTITUICIONAL("Emenda à Constituição"),
	//
	LEI_COMPLEMENTAR("Lei Complementar"),
	//
	LEI_ORDINARIA("Lei Ordnária"),
	//
	LEI_DELEGADA("Lei Delegada"),
	//
	MEDIDA_PROVISORIA("Medida Provisória"),
	//
	DECRETO_LEGISLATIVO("Decreto Legislativo"),
	//
	RESOLUCAO("Resolução");

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
