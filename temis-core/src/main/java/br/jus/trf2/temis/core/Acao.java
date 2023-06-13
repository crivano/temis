package br.jus.trf2.temis.core;

import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.iam.model.Agente;

public interface Acao<E extends IEntidade, A extends Agente, V extends Evento<E, V>>
		extends IJuiaAction<E, A, V, Etiqueta> {

	default String getClick(E element, V event) {
		return "this.actQuery('" + this.getName() + "'," + (event != null ? event.getId() : "null") + ")";
	}

}
