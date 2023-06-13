package br.jus.trf2.temis.pjd.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.annotations.ShowGroup;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.util.NoSerialization;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.pjd.model.enm.TipoDeJurisprudencia;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
@Global(singular = "Jurisprudência", plural = "Jurisprudências", gender = Gender.SHE, locator = "pro-jurisprudencia", codePrefix = "JU", deletable = false)
public class Jurisprudencia extends Entidade {
	@Entity
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
	@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 128)
	@Data
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Evento de Jurisprudência", plural = "Eventos de Jurisprudências", gender = Gender.HE, codePrefix = "EN")
	public abstract static class EventoJurisprudencia extends Evento<Jurisprudencia, EventoJurisprudencia> {
		@NoSerialization
		@ManyToOne(fetch = FetchType.LAZY)
		private Jurisprudencia norma;
	}

	@OneToMany(mappedBy = EventoJurisprudencia.Fields.norma, cascade = CascadeType.ALL)
	@OrderBy(Evento.Fields.dtIni)
	private SortedSet<EventoJurisprudencia> evento = new TreeSet<>();

	@Transient
	@NotNull
	@Edit(caption = "Tipo de Jurisprudência", colM = 12)
	@Enumerated(EnumType.STRING)
	TipoDeJurisprudencia tipoJurisprudencia;

	@Search
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Sigla do Tribunal", colM = 3)
	String siglaTribunal;

	@Search
	@Show
	@NotNull
	@Edit(caption = "Nome do Órgao Julgador", colM = 9)
	String nomeOrgaoJulgador;

	@Search
	@Show
	@NotNull
	@Edit(caption = "N&ordm; do Processo", colM = 6)
	String numeroProcesso;

	@Show
	@Edit(caption = "N&ordm; Antigo do Processo", colM = 6)
	String numeroAntigoDoProcesso;

	@Search
	@Show
	@NotNull
	@Edit(caption = "Data do Julgamento", colM = 6)
	LocalDate dataJulgamento;

	@Show
	@NotNull
	@Edit(caption = "Data da Publicação", colM = 6)
	LocalDate dataPublicacao;

	@Show
	@Search
	@NotNull
	@Edit(caption = "Nome do Relator", colM = 9)
	String nomeRelator;

	@Show
	@NotNull
	@Edit(caption = "Ementa", kind = EditKindEnum.TEXTAREA, attr = "rows=3", colM = 12)
	@Column(length = 2048)
	String textoEmenta;

	@Show
	@NotNull
	@Column(length = 2048)
	@Edit(caption = "Certidão de Julgamento", kind = EditKindEnum.TEXTAREA, attr = "rows=3", colM = 12)
	String textoCertidaoDeJulgamento;

	@Override
	public String getDescr() {
		String s = "";
		if (!Utils.empty(siglaTribunal)) {
			if (!Utils.empty(s))
				s += ", ";
			s += siglaTribunal;
		}
		if (!Utils.empty(nomeOrgaoJulgador)) {
			if (!Utils.empty(s))
				s += ", ";
			s += nomeOrgaoJulgador;
		}
		if (!Utils.empty(numeroProcesso)) {
			if (!Utils.empty(s))
				s += ", ";
			s += "processo " + numeroProcesso + "&ordm;";
		}
		if (!Utils.empty(numeroAntigoDoProcesso)) {
			if (!Utils.empty(s))
				s += ", ";
			s += "acórdão " + numeroAntigoDoProcesso;
		}
		return s;
	}
}
