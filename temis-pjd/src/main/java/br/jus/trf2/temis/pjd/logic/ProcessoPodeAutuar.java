package br.jus.trf2.temis.pjd.logic;

import com.crivano.jlogic.And;
import com.crivano.jlogic.CompositeExpressionSuport;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.Not;

import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeParte;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class ProcessoPodeAutuar extends CompositeExpressionSuport {
	Processo processo;

	@Override
	protected Expression create() {
		Expression e = And.of(Not.of(ProcessoEstaAutuado.of(processo)), ProcessoTemAutorEReu.of(processo),
				ProcessoTemPedido.of(processo), ProcessoTemFato.of(processo));
		for (EventoProcessualInclusaoDeParte p : processo.getEventos(EventoProcessualInclusaoDeParte.class)) {
			if (p.isAutor()) {
				e = And.of(e, Not.of(ParteDeveIncluirFontePagadora.of(processo, p)),
						Not.of(ParteDeveIncluirDocumentoComprobatorioDeEPP.of(processo, p)));
			}
		}
		return e;
	}
}
