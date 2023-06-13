package br.jus.trf2.temis.cae.model;

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
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Menu;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.annotations.ShowGroup;
import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.cae.model.enm.CaeEspecieDeAtividadeEnum;
import br.jus.trf2.temis.cae.model.enm.CaeModalidadeEnum;
import br.jus.trf2.temis.cae.model.enm.CaeOrgaoEnum;
import br.jus.trf2.temis.cae.model.enm.CaeParticipacaoEnum;
import br.jus.trf2.temis.cae.model.enm.CaeTipoDeAtividadeEnum;
import br.jus.trf2.temis.cae.model.enm.CaeTurnoEnum;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeAprovacao;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeDeferimento;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeIndeferimento;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeInscricao;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeReprovacao;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.action.Auditar;
import br.jus.trf2.temis.core.action.Editar;
import br.jus.trf2.temis.core.util.NoSerialization;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
@Menu(list = true)
@Global(singular = "Atividade", plural = "Atividades", gender = Gender.SHE, locator = "cae-atividade", codePrefix = "AT", deletable = true)
public class CaeAtividade extends Entidade {

	@Entity
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
	@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 64)
	@Data
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Evento de Atividade", plural = "Eventos de Atividades", gender = Gender.HE, codePrefix = "EA")
	public abstract static class CaeEventoDeAtividade extends Evento<CaeAtividade, CaeEventoDeAtividade> {
		@NoSerialization
		@ManyToOne(fetch = FetchType.LAZY)
		private CaeAtividade atividade;

		public <T extends CaeEventoDeAtividade> boolean temReferenciaDaClasse(Class<T> clazz) {
			for (T p : getAtividade().getEventos(clazz)) {
				if (p.getReferente() == this)
					return true;
			}
			return false;
		}

		public boolean isDeferida() {
			return temReferenciaDaClasse(CaeEventoDeAtividadeDeferimento.class);
		}

		public boolean isIndeferida() {
			return temReferenciaDaClasse(CaeEventoDeAtividadeIndeferimento.class);
		}

		public boolean isAprovada() {
			return temReferenciaDaClasse(CaeEventoDeAtividadeAprovacao.class);
		}

		public boolean isReprovada() {
			return temReferenciaDaClasse(CaeEventoDeAtividadeReprovacao.class);
		}
	}

	@OneToMany(mappedBy = CaeEventoDeAtividade.Fields.atividade, cascade = CascadeType.ALL)
	@OrderBy(Evento.Fields.dtIni)
	private SortedSet<CaeEventoDeAtividade> evento = new TreeSet<>();

	@Search
	@NotNull
	@Edit(caption = "Espécie", colM = 3)
	CaeEspecieDeAtividadeEnum especie;

	@Search
	@NotNull
	@Edit(caption = "Orgão", colM = 3)
	CaeOrgaoEnum orgao;

	@Search
	@NotNull
	@Edit(caption = "Tipo", colM = 3)
	CaeTipoDeAtividadeEnum tipo;

	@Search
	@Show
	@Edit(caption = "Temática", colM = 3)
	@ManyToOne(fetch = FetchType.LAZY)
//	@NotNull
	CaeTematica tematica;

	@NotNull
	@Edit(caption = "Participações", colM = 3)
	CaeParticipacaoEnum participacao;

	@Search
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Tema", colM = 6)
	String tema;

	@Search
	@NotNull
	@Edit(caption = "Número de Vagas", colM = 3)
	Integer vagas;

	@NotNull
	@Edit(caption = "Carga Horária CAE", colM = 3)
	Integer cargaHorariaCae;

	@NotNull
	@Edit(caption = "Carga Horária", colM = 3)
	Integer cargaHoraria;

	@NotNull
	@Edit(caption = "Carga Horária OAB", colM = 3)
	Integer cargaHorariaOab;

	@NotNull
	@Edit(caption = "Local", colM = 3)
	String local;

	@NotNull
	@Edit(caption = "Modalidade", colM = 3)
	CaeModalidadeEnum modalidade;

	@NotNull
	@Edit(colM = 3)
	CaeTurnoEnum turno;

	@NotNull
	@Edit(colM = 3)
	String hora;

	@Search
	@FieldSet(caption = "Datas")
	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeInicio;

	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeFim;

	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeAberturaDasInscricoes;

	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeEncerramentoDasInscricoes;

	@FieldSet(caption = "Informações Complementares")
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Público Alvo", kind = EditKindEnum.TEXTAREA, colM = 12)
	String publicoAlvo;

	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Descrição", kind = EditKindEnum.TEXTAREA, colM = 12)
	String descricao;

	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Observações", kind = EditKindEnum.TEXTAREA, colM = 12)
	String obs;

	@FieldSet(caption = "Informações de Controle")
	@Edit(caption = "Publicar Atividade", colM = 4)
	boolean publicarAtividade;
	@Edit(colM = 4)
	boolean ativarDetalhamento;
	@Edit(colM = 4)
	boolean cancelada;

	@Override
	public String getCode() {
		return getId().toString();
	}

	@Override
	public String getDescr() {
		return descricao;
	}

	@Override
	public String getDescrCompleta() {
		return descricao;

	}

	@Override
	public String getSelectFirstLine() {
		return descricao;

	}

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		// set.add(new Editar<Processo>());
		set.add(new Editar());
		set.add(new Auditar());
		set.add(new CaeEventoDeAtividadeInscricao());
	}

	public <T extends CaeEventoDeAtividade> SortedSet<T> getEventos(Class<T> clazz) {
		TreeSet<T> set = new TreeSet<T>();
		if (getEvento() == null || getEvento().size() == 0)
			return set;

		for (Evento e : getEvento())
			if (clazz.isAssignableFrom(e.getClass()))
				set.add((T) e);
		return set;
	}

}
