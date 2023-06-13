package br.jus.trf2.temis.cae.logic;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

import br.jus.trf2.temis.cae.model.CaeAtividade;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class CaeAtivadeEPublica implements Expression {
	CaeAtividade atividade;

	@Override
	public boolean eval() {
		return atividade.isPublicarAtividade();
	}

	@Override
	public String explain(boolean result) {
		return JLogic.explain("é pública", result);
	}
}
