package br.jus.trf2.temis.core.action;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.util.ContextInterceptor;
import br.jus.trf2.temis.iam.model.Agente;
import lombok.Data;

@Data
@Global(singular = "Exclusão", plural = "Exclusões", gender = Gender.SHE, action = "Excluir", icon = "far fa-trash-alt")
public class ExcluirMiniAction<E extends Entidade, T extends Evento<E, T>> implements Acao<E, Agente, T> {

	@Override
	public String getConfirmation(Entidade entity, Evento event) {
		Global global = event.getClass().getAnnotation(Global.class);
		if (global != null && global.singular() != null && global.gender() != null)
			return "Tem certeza que deseja excluir " + (global.gender() == Gender.HE ? "o" : "a") + " "
					+ global.singular().toLowerCase() + "?";

		return "Tem certeza que deseja excluir '" + event.toString() + "'?";
	}

	@Override
	public void execute(Agente actor, Agente onBehalfOf, E entity, T event, Etiqueta tag) throws Exception {
		entity.removeChange(event);
		ContextInterceptor.getDao().persist(entity);
	}

}
