package br.jus.trf2.temis.pjd.model.enm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TipoDePoloEnum {
	ATIVO("Ativo", true),
	//
	PASSIVO("Passivo", true),
	//
	OUTROS_PARTICIPANTES("Outros Participantes", true);

	private final String nome;
	private final boolean ativo;
}
