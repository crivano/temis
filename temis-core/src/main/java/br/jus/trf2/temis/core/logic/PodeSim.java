package br.jus.trf2.temis.core.logic;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.JLogic;

public class PodeSim implements Expression {
	@Override
	public boolean eval() {
		return true;
	}

	@Override
	public String explain(boolean result) {
		return JLogic.explain("sempre pode", result);
	}
};