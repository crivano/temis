package br.jus.trf2.temis.pjd.model.event;

import java.util.SortedSet;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.pjd.model.PeticaoInicial;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("1")
@Data
@NoArgsConstructor
@Global(singular = "Anotação", plural = "Anotações", gender = Gender.SHE, action = "Anotar", icon = "far fa-sticky-note")
public class PeticaoInicialEventoAnotacao extends PeticaoInicial.XEvento {
	@Edit(caption = "Texto da Anotação", colXS = 12)
	String texto;

	@Override
	public String getDescr() {
		return texto;
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
	}

}
