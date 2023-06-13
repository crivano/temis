package br.jus.trf2.temis.pjd.logic;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeParte;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class ParteContemDocumentoComprobatorioDeEpp implements Expression {
	EventoProcessualInclusaoDeParte evento;

	@Override
	public boolean eval() {
		return evento.isContemDocumentoComprobatorioDeEpp();
	}

	@Override
	public String explain(boolean result) {
		return JLogic.explain("contém documento comprobatório de empresa de pequeno porte", result);
	}
}
