package br.jus.trf2.temis.pjd.logic;

import com.crivano.jlogic.And;
import com.crivano.jlogic.CompositeExpressionSuport;
import com.crivano.jlogic.Expression;

import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeParte;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class ParteDeveIncluirFontePagadora extends CompositeExpressionSuport {
	Processo processo;
	EventoProcessualInclusaoDeParte evento;

	@Override
	protected Expression create() {
		return And.of(PartePodeIncluirFontePagadora.of(processo, evento),
				ProcessoTemPedidoComVerbaSalarial.of(processo));
	}

}
