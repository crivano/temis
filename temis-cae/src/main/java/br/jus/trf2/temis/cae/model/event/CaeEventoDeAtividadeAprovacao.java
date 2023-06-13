package br.jus.trf2.temis.cae.model.event;

import java.util.SortedSet;

import javax.persistence.Entity;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.cae.model.CaeAtividade.CaeEventoDeAtividade;
import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.CancelarMiniAction;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.core.util.DescrBuilder;
import br.jus.trf2.temis.crp.model.CrpPessoa;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Aprovação", plural = "Aprovações", gender = Gender.SHE, action = "Aprovar", icon = "fas fa-check")
public class CaeEventoDeAtividadeAprovacao extends CaeEventoDeAtividade {

	@Override
	public String getDescr() {
		CaeEventoDeAtividadeDeferimento deferimento = (CaeEventoDeAtividadeDeferimento) getReferente();
		CaeEventoDeAtividadeInscricao inscricao = (CaeEventoDeAtividadeInscricao) deferimento.getReferente();

		@NonNull
		CrpPessoa pessoa = inscricao.getPessoa();
		return DescrBuilder.builder().add("", pessoa.getDescrCompleta()).build();
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
		set.add(new CancelarMiniAction());
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}

}
