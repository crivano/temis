package br.jus.trf2.temis.pjd.model.event;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.pjd.model.Processo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Anotação", plural = "Anotações", gender = Gender.SHE, action = "Anotar", icon = "far fa-sticky-note")
public class EventoProcessualAnotacao extends Processo.EventoProcessual {
	@Required
	@NonNull
	@Edit(caption = "Texto da Anotação", colXS = 12, attr = "validate=required")
	@Column(length = 2048)
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
