package br.jus.trf2.temis.pjd.model.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Tipo de exibição de marcador", plural = "Tipos de exibição de marcador", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum TipoDePedidoEnum implements IEnum {
	CONDENAR_A_PAGAR("Condenar a pagar uma quantia"),
	//
	CONDENAR_A_FAZER("Condenar a fazer"),
	//
	CONDENAR_A_DEIXAR_DE_FAZER("Condenar a deixar de fazer"),
	//
	CONSTITUIR_RELACAO_JURIDICA("Constituir relação jurídica"),
	//
	ANULAR_RELACAO_JURIDICA("Anular relação jurídica"),
	//
	DECLARAR_EXISTENCIA_DE_FATO("Declarar existência de fato real"),
	//
	DECLARAR_INEXISTENCIA_DE_FATO("Declarar inexistência de fato real");

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
