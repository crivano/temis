package br.jus.trf2.temis.cae.model.event;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.crivano.jlogic.Expression;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.cae.logic.CaePodeDeferirParticipacaoEmAtividade;
import br.jus.trf2.temis.cae.logic.CaePodeIndeferirParticipacaoEmAtividade;
import br.jus.trf2.temis.cae.model.CaeAtividade;
import br.jus.trf2.temis.cae.model.CaeAtividade.CaeEventoDeAtividade;
import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.CancelarMiniAction;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.core.util.DescrBuilder;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.crp.model.CrpPessoa;
import br.jus.trf2.temis.iam.model.Agente;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inscrição de Participante", plural = "Inscrições de Participantes", gender = Gender.SHE, action = "Inscrever Participante", icon = "fas fa-plus")
@FieldNameConstants
public class CaeEventoDeAtividadeInscricao extends CaeEventoDeAtividade {
	@Edit(caption = "Participante", colM = 12)
	@FullSerialization
	@NonNull
	@ManyToOne
	CrpPessoa pessoa;

	@Override
	public String getDescr() {
		return DescrBuilder.builder().add("", pessoa.getDescrCompleta()).build();
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
//		set.add(new CancelarMiniAction());
		set.add(new CaeEventoDeAtividadeDeferimento());
		set.add(new CaeEventoDeAtividadeIndeferimento());
	}

	@Override
	public Expression getActiveMiniAction(Agente actor, Agente onBehalfOf, CaeAtividade element, Acao miniAction) {
		if (miniAction instanceof CaeEventoDeAtividadeDeferimento)
			return CaePodeDeferirParticipacaoEmAtividade.of(this);
		if (miniAction instanceof CaeEventoDeAtividadeIndeferimento)
			return CaePodeIndeferirParticipacaoEmAtividade.of(this);
		return super.getActiveMiniAction(actor, onBehalfOf, element, miniAction);
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}

}
