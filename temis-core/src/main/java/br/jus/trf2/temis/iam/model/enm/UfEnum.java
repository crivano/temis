package br.jus.trf2.temis.iam.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Unidade Federativa", plural = "Unidades Federativas", gender = Gender.SHE)
@AllArgsConstructor
@Getter
public enum UfEnum implements IEnum {
	AC("Acre"), AL("Alagoas"), AM("Amazonas"), AP("Amapá"), BA("Bahia"), CE("Ceará"), DF("Distrito Federal"),
	ES("Espirito Santo"), GO("Goias"), MA("Maranhão"), MG("Minas Gerais"), MS("Mato Grosso Sul"), MT("Mato Grosso"),
	PA("Pará"), PB("Paraiba"), PE("Pernanbuco"), PI("Piaui"), PR("Parana"), RJ("Rio de Janeiro"),
	RN("Rio Grande do Norte"), RO("Rondônia"), RR("Roraima"), RS("Rio Grande do Sul"), SC("Santa Catarina"),
	SE("Sergipe"), SP("São Paulo"), TO("Tocantins");

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
