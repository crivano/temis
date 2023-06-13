package br.jus.trf2.temis.pjd.logic;

import com.crivano.jlogic.And;
import com.crivano.jlogic.CompositeExpressionSuport;
import com.crivano.jlogic.Expression;

import br.jus.trf2.temis.pjd.model.Processo;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class ProcessoTemAutorEReu extends CompositeExpressionSuport {
	Processo processo;

	@Override
	protected Expression create() {
		return And.of(ProcessoTemAutor.of(processo), ProcessoTemReu.of(processo));
	}

}
