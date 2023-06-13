package br.jus.trf2.temis.cae.model.enm;

import com.crivano.jbiz.IEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CaeMultiplicadorDeConvolacaoEnum implements IEnum {
	X1(1, "1"),
	//
	X2(2, "2"),
	//
	X3(3, "3"),
	//
	X4(4, "4"),
	//
	X5(5, "5"),
	//
	X6(6, "6"),
	//
	X7(7, "7"),
	//
	X8(8, "8"),
	//
	X9(9, "9"),
	//
	X10(10, "10");

	private final int multiplicador;
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
