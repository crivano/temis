package br.jus.trf2.temis.pjd.model.event;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Arquivo;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.core.action.ExibirPdf;
import br.jus.trf2.temis.pjd.model.Processo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Documento", plural = "Inclusões de Documentos", gender = Gender.SHE, action = "Incluir Documento", icon = "fas fa-paperclip")
public class EventoProcessualInclusaoDeDocumento extends Processo.EventoProcessual
		implements IEventoProcessualComArquivo {

	@ManyToOne
	@Edit(caption = "Arquivo PDF", colM = 12, kind = EditKindEnum.FILE, attr = "validate=required")
	Arquivo arquivo;

	@Required
	@NonNull
	@Edit(caption = "Título", colM = 8)
	String titulo;

	@Required
	@NonNull
	@Edit(caption = "Data", colM = 4, attr = "validate=required")
	LocalDate data;

	@NonNull
	@Edit(caption = "Observações", kind = EditKindEnum.TEXTAREA, colM = 12)
	@Column(length = 2048)
	String texto;

	@Override
	public String getDescr() {
		if (texto != null)
			return titulo + ": " + texto;
		return titulo;
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(ExibirPdf.of(this));
		set.add(new ExcluirMiniAction());
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}

}
