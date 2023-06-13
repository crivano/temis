package br.jus.trf2.temis.pjd.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.joda.money.Money;

import com.crivano.jbiz.ITag;
import com.crivano.juia.annotations.Detail;
import com.crivano.juia.annotations.DetailGroup;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Menu;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.action.Auditar;
import br.jus.trf2.temis.core.enm.MarcadorEnum;
import br.jus.trf2.temis.core.util.NoSerialization;
import br.jus.trf2.temis.pjd.model.enm.TipoDePedidoEnum;
import br.jus.trf2.temis.pjd.model.enm.TipoDeVerbaEnum;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualAnotacao;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualAutuacao;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualDefinicaoDeValorDaCausa;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeFato;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeJurisprudencia;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeNorma;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeParte;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDePedido;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
//@Menu(create = true)
@Global(singular = "Processo", plural = "Processos", gender = Gender.HE, locator = "pro-processo", codePrefix = "PR", deletable = true)
public class Processo extends Entidade {
//	Endereçamento
//	Identificação do autor (nome, nacionalidade, estado civil, RG, CPF e residência)
//	Classe da ação (ou seria o assunto? O exemplo é: Ação de Obrigação de Fazer C/C Danos Morais)
//	Identificação do réu
//	Fatos
//	Do Dano
//	Do Direito
//	Jurisprudência
//	Do Pedido
//	Provas
//	Valor da Causa
//	Fecho (Pelo deferimento e data)
//	Assinatura (nome e OAB)

	@Entity
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
	@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 64)
	@Data
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Evento Processual", plural = "Eventos Processuais", gender = Gender.HE, codePrefix = "EP")
	public abstract static class EventoProcessual extends Evento<Processo, EventoProcessual> {
		@NoSerialization
		@ManyToOne(fetch = FetchType.LAZY)
		private Processo processo;
	}

	@OneToMany(mappedBy = EventoProcessual.Fields.processo, cascade = CascadeType.ALL)
	@OrderBy(Evento.Fields.dtIni)
	private SortedSet<EventoProcessual> evento = new TreeSet<>();

	@DetailGroup(caption = "Informações")
	@Detail
	@Search
//	@Edit(caption = "Valor da Causa", colM = 4, colL = 3)
	Money valorDaCausa;

//	@Edit(caption = "Segredo de Justiça", colM = 4, colL = 3)
	boolean segredoDeJustica;
//	@Edit(caption = "Justiça Gratuita", colM = 4, colL = 3)
	boolean justicaGratuita;
//	@Edit(caption = "Tutela ou Liminar", colM = 4, colL = 3)
	boolean tutelaOuLiminar;

	// List<XAssunto> xAssunto;
//	List<XPrioridade> xPrioridade;

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		// set.add(new Editar<Processo>());
		set.add(new EventoProcessualAnotacao());
		set.add(new EventoProcessualInclusaoDeParte());
		set.add(new EventoProcessualInclusaoDeFato());
		set.add(new EventoProcessualInclusaoDeNorma());
		set.add(new EventoProcessualInclusaoDeJurisprudencia());
		set.add(new EventoProcessualInclusaoDePedido());
		set.add(new EventoProcessualDefinicaoDeValorDaCausa());
		set.add(new EventoProcessualAutuacao());
		set.add(new Auditar());
	}

	@Override
	public void addTags(SortedSet<ITag> set) {
		super.addTags(set);

		if (!isAutuado())
			set.add(Etiqueta.of(null, this, null, getPessoaCadastrante(), getUnidadeCadastrante(),
					MarcadorEnum.EM_ELABORACAO, this.getBegin(), null));
		if (isAutuado())
			set.add(Etiqueta.of(null, this, null, getPessoaCadastrante(), getUnidadeCadastrante(),
					MarcadorEnum.AGUARDANDO_DECISAO, this.getBegin(), null));
	}

	public boolean isTemPedido() {
		return getLastEvent(EventoProcessualInclusaoDePedido.class);
	}

	public boolean isTemPedidoComVerbaSalarial() {
		for (EventoProcessualInclusaoDePedido p : getEventos(EventoProcessualInclusaoDePedido.class)) {
			if (p.getTipoDePedido() == TipoDePedidoEnum.CONDENAR_A_PAGAR && p.getVerba() == TipoDeVerbaEnum.SALARIO)
				return true;
		}
		return false;
	}

	public boolean isTemFato() {
		return getLastEvent(EventoProcessualInclusaoDeFato.class);
	}

	public boolean isAutuado() {
		return getLastEvent(EventoProcessualAutuacao.class);
	}

	private boolean getLastEvent(Class<? extends EventoProcessual> clazz) {
		if (getEvento() == null || getEvento().size() == 0)
			return false;
		for (EventoProcessual e : getEvento())
			if (clazz.isAssignableFrom(e.getClass()))
				return true;
		return false;
	}

	public <T extends EventoProcessual> SortedSet<T> getEventos(Class<T> clazz) {
		TreeSet<T> set = new TreeSet<T>();
		if (getEvento() == null || getEvento().size() == 0)
			return set;

		for (Evento e : getEvento())
			if (clazz.isAssignableFrom(e.getClass()))
				set.add((T) e);
		return set;
	}

	public boolean isTemAutor() {
		for (EventoProcessualInclusaoDeParte e : getEventos(EventoProcessualInclusaoDeParte.class)) {
			if (e.isAutor())
				return true;
		}
		return false;
	}

	public boolean isTemReu() {
		for (EventoProcessualInclusaoDeParte e : getEventos(EventoProcessualInclusaoDeParte.class)) {
			if (e.isReu())
				return true;
		}
		return false;
	}

}
