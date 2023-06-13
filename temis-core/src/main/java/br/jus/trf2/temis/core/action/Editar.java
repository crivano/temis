package br.jus.trf2.temis.core.action;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.iam.model.Agente;
import lombok.Data;

@Data
@Global(singular = "Edição", plural = "Edições", gender = Gender.SHE, action = "Editar", icon = "fa fa-edit")
public class Editar<E extends Entidade, T extends Evento<E, T>> implements Acao<E, Agente, T> {

	@Override
	public String getClick(E entidade, T evento) {
		return "this.edit()";
	}
}
