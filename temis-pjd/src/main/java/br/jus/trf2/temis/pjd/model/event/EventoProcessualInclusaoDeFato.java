package br.jus.trf2.temis.pjd.model.event;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.joda.time.LocalDate;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.Not;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.pjd.logic.ProcessoTemFato;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.Processo.EventoProcessual;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Fato", plural = "Inclusões de Fatos", gender = Gender.SHE, action = "Incluir Fato", icon = "fas fa-plus")
public class EventoProcessualInclusaoDeFato extends Processo.EventoProcessual {
	@Edit(caption = "Título", colM = 8)
	String titulo;

	@Edit(caption = "Data de Referência", colM = 4)
	LocalDate dataDeReferencia;

	@Required
	@NonNull
	@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA, colM = 12)
	@Column(length = 2048)
	String texto;

	@Override
	public String getDescr() {
		return titulo + ": " + texto;
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
		set.add(new EventoProcessualInclusaoDeDocumentoGenerico());
	}

	@Override
	public Expression getRequired(Processo processo, EventoProcessual event) {
		return Not.of(ProcessoTemFato.of(processo));
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}

}
