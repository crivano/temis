package br.jus.trf2.temis.pjd.logic;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import br.jus.trf2.temis.pjd.model.Processo;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class ProcessoTemAutor implements Expression {
	Processo processo;

	@Override
	public boolean eval() {
		return processo.isTemAutor();
	}

	@Override
	public String explain(boolean result) {
		return JLogic.explain("tem parte ativa", result);
	}
}
