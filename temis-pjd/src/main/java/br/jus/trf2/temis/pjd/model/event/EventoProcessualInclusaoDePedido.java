package br.jus.trf2.temis.pjd.model.event;

import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.joda.money.Money;

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
import br.jus.trf2.temis.pjd.logic.ProcessoTemPedido;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.Processo.EventoProcessual;
import br.jus.trf2.temis.pjd.model.enm.TipoDeLiminarEnum;
import br.jus.trf2.temis.pjd.model.enm.TipoDePedidoEnum;
import br.jus.trf2.temis.pjd.model.enm.TipoDeVerbaEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Pedido", plural = "Inclusões de Pedidos", gender = Gender.SHE, action = "Incluir Pedido", icon = "fas fa-plus")
public class EventoProcessualInclusaoDePedido extends Processo.EventoProcessual {

	@Required
	@NonNull
	@Edit(caption = "Tipo de Pedido", colM = 6)
	@Enumerated(EnumType.STRING)
	TipoDePedidoEnum tipoDePedido;

	@Required
	@NonNull
	@Edit(caption = "Liminar", colM = 6)
	@Enumerated(EnumType.STRING)
	TipoDeLiminarEnum liminar;

	@Required
	@Edit(caption = "Tipo de Verba", colM = 6, attrContainer = "v-if=act.tipoDePedido == 'CONDENAR_A_PAGAR'")
	@Enumerated(EnumType.STRING)
	TipoDeVerbaEnum verba;

	@Required
	@Edit(caption = "Valor", colM = 6, attrContainer = "v-if=act.tipoDePedido == 'CONDENAR_A_PAGAR'", attr = "validate=required")
	Money valor;

	@Required
	@NonNull
	@Edit(caption = "Pedido", kind = EditKindEnum.TEXTAREA, attr = "rows=4", colM = 12, hint = "Escreva de forma clara e objetiva.")
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

	@Override
	public Expression getRequired(Processo processo, EventoProcessual event) {
		return Not.of(ProcessoTemPedido.of(processo));
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}
}
