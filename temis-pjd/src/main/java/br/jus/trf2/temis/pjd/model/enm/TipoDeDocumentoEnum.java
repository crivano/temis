package br.jus.trf2.temis.pjd.model.enm;

import com.crivano.jbiz.IEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDeDocumentoEnum implements IEnum {
	RG_COM_CPF("Carteira de Identidade com CPF"),
	//
	RG("Carteira de Identidade"),
	//
	CPF("CPF - Cadastro Nacional de Pessoas Físicas"),
	//
	CERTIDAO_DE_NASCIMENTO("Certidão de Nascimento"),
	//
	OAB("Inscrição na Órdem dos Advogados do Brasil"),
	//
	CARTAO_DO_CNPJ("Cadastro Nacional de Pessoas Jurídicas"),
	//
	PASS("Passaporte"),
	//
	COMPROVANTE_DE_RESIDENCIA("Comprovante de Residência"),
	//
	PROVA("Prova"),
	//
	OUTRO("Outros");

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
