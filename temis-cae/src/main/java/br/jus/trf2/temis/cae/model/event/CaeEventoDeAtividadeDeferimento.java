package br.jus.trf2.temis.cae.model.event;

import java.util.SortedSet;

import javax.persistence.Entity;

import com.crivano.jlogic.Expression;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.cae.logic.CaeParticipacaoEmAtividadeEstaDeferida;
import br.jus.trf2.temis.cae.logic.CaePodeAprovarParticipacaoEmAtividade;
import br.jus.trf2.temis.cae.logic.CaePodeDeferirParticipacaoEmAtividade;
import br.jus.trf2.temis.cae.logic.CaePodeIndeferirParticipacaoEmAtividade;
import br.jus.trf2.temis.cae.logic.CaePodeReprovarParticipacaoEmAtividade;
import br.jus.trf2.temis.cae.model.CaeAtividade;
import br.jus.trf2.temis.cae.model.CaeAtividade.CaeEventoDeAtividade;
import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.CancelarMiniAction;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.core.util.DescrBuilder;
import br.jus.trf2.temis.crp.model.CrpPessoa;
import br.jus.trf2.temis.iam.model.Agente;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Deferimento de Inscrição", plural = "Deferimentos de Inscrições", gender = Gender.HE, action = "Deferir", icon = "fas fa-calendar-plus")
public class CaeEventoDeAtividadeDeferimento extends CaeEventoDeAtividade {

	@Override
	public String getDescr() {
		CrpPessoa pessoa = ((CaeEventoDeAtividadeInscricao) getReferente()).getPessoa();
		return DescrBuilder.builder().add("", pessoa.getDescrCompleta()).build();
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
		set.add(new CancelarMiniAction());
		set.add(new CaeEventoDeAtividadeAprovacao());
		set.add(new CaeEventoDeAtividadeReprovacao());
	}

	@Override
	public Expression getActiveMiniAction(Agente actor, Agente onBehalfOf, CaeAtividade element, Acao miniAction) {
		if (miniAction instanceof CaeEventoDeAtividadeAprovacao)
			return CaePodeAprovarParticipacaoEmAtividade.of(this);
		if (miniAction instanceof CaeEventoDeAtividadeReprovacao)
			return CaePodeReprovarParticipacaoEmAtividade.of(this);
		return super.getActiveMiniAction(actor, onBehalfOf, element, miniAction);
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}
}
