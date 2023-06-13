package br.jus.trf2.temis.pjd.model.event;

import java.io.InputStream;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.crivano.jlogic.Expression;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Arquivo;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.action.ExibirPdf;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.Agente;
import br.jus.trf2.temis.pjd.logic.ProcessoPodeAutuar;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.Processo.EventoProcessual;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Autuação", plural = "Autuações", gender = Gender.SHE, action = "Autuar", icon = "fas fa-signature")
public class EventoProcessualAutuacao extends Processo.EventoProcessual implements IEventoProcessualComArquivo {

	@Required
	@NonNull
	@Edit(caption = "Senha", attr = "type=password", hint = "Digite a sua senha para confirmar a autuação", colM = 12)
	@Column(length = 20)
	String senhaUsuario;

	@ManyToOne
	Arquivo arquivo;

	@Override
	public String getDescr() {
		return "Petição Inicial Autuada";
	}

	@Override
	public Expression getActive(Processo processo, EventoProcessual evento) {
		return ProcessoPodeAutuar.of(processo);
	}

	@Override
	public Expression getRequired(Processo processo, EventoProcessual evento) {
		return ProcessoPodeAutuar.of(processo);
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		super.addMiniActions(set);
		set.add(ExibirPdf.of(this));
	}

	@Override
	public void execute(Agente actor, Agente onBehalfOf, Processo entity, EventoProcessual event, Etiqueta tag)
			throws Exception {
		InputStream stream = this.getClass().getResourceAsStream("/peticao-inicial.pdf");
		byte[] bytes = Utils.convertStreamToByteArray(stream, 2048);
		arquivo = Arquivo.of("application/pdf", "peticao-inicial.pdf", bytes);
		dao().persist(arquivo);
		super.execute(actor, onBehalfOf, entity, event, tag);
	}

}
