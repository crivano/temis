package br.jus.trf2.temis.cae.logic;

import com.crivano.jlogic.And;
import com.crivano.jlogic.CompositeExpressionSuport;
import com.crivano.jlogic.Expression;
import com.crivano.jlogic.Not;

import br.jus.trf2.temis.cae.model.CaeAtividade.CaeEventoDeAtividade;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class CaePodeAprovarParticipacaoEmAtividade extends CompositeExpressionSuport {
	CaeEventoDeAtividade evento;

	@Override
	protected Expression create() {
		Expression e = And.of(

				Not.of(CaeParticipacaoEmAtividadeEstaAprovada.of(evento)),

				Not.of(CaeParticipacaoEmAtividadeEstaReprovada.of(evento)));

		return e;
	}
}
