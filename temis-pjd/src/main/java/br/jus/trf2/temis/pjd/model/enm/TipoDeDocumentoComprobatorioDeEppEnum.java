package br.jus.trf2.temis.pjd.model.enm;

import com.crivano.jbiz.IEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDeDocumentoComprobatorioDeEppEnum implements IEnum {
	CARTAO_DO_CNPJ("Cartão do CNPJ");

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
