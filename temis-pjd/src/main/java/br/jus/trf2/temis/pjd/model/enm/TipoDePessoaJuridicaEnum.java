package br.jus.trf2.temis.pjd.model.enm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDePessoaJuridicaEnum {
	PROCURADORIA("PROC", "Procuradoria", null, true),
	//
	ESCRITORIO_DE_ADVOCACIA("ESCR", "Escritório de Advocacia", null, true),
	//
	DEFENSORIA("DEF", "Procuradoria", null, true);

	String codigo;
	String nome;
	TipoDePessoaJuridicaEnum tipoDePessoaJuridicaPai;
	boolean ativo;
}
