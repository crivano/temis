package br.jus.trf2.temis.cae.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.crivano.jsync.IgnoreForDependecyLevel;
import com.crivano.jsync.IgnoreForSimilarity;
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

import br.jus.trf2.temis.cae.model.enm.CaeTipoDeAvaliacaoDeDesempenhoEnum;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.action.Auditar;
import br.jus.trf2.temis.core.action.Editar;
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
@Global(singular = "Curso", plural = "Cursos", gender = Gender.HE, locator = "cae-curso", codePrefix = "CS", deletable = false)
public class CaeCurso extends Entidade {

	@Search
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Nome", colM = 6)
	String nome;

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Temática", plural = "Temáticas", gender = Gender.SHE, locator = "pro-curso-x-tematica", codePrefix = "CT", deletable = true)
	public static class XTematica extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Search
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private CaeCurso curso;

		@Search
		@Show
		@Edit(caption = "Temática", colM = 8)
		@ManyToOne(fetch = FetchType.LAZY)
		CaeTematica tematica;

		@Edit(caption = "Observações", colM = 4)
		String obs;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Atividade", plural = "Atividades", gender = Gender.SHE, locator = "pro-curso-x-atividade", codePrefix = "CA", deletable = true)
	public static class XAtividade extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Search
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private CaeCurso curso;

		@Search
		@Show
		@Edit(caption = "Atividade", colM = 8)
		@ManyToOne(fetch = FetchType.LAZY)
		CaeAtividade atividade;

		@Edit(caption = "Observações", colM = 4)
		String obs;
	}

	// Endereços
	@IgnoreForDependecyLevel
	@IgnoreForSimilarity
	@OneToMany(mappedBy = "tematica", cascade = CascadeType.ALL)
	@Show(caption = "Temática", value = "__fieldname__.logradouro + ' ' + __fieldname__.numero + (__fieldname__.complemento ? '/' + __fieldname__.complemento : '') + ', ' + __fieldname__.bairro + ', ' + __fieldname__.localidade + '/' + __fieldname__.uf + '. CEP: ' + __fieldname__.cep")
	@FieldSet(caption = "Comissões Temáticas")
	@Edit()
	private List<XTematica> tematica = new ArrayList<>();

	// Endereços
	@IgnoreForDependecyLevel
	@IgnoreForSimilarity
	@OneToMany(mappedBy = "atividade", cascade = CascadeType.ALL)
	@Show(caption = "Atividade", value = "__fieldname__.logradouro + ' ' + __fieldname__.numero + (__fieldname__.complemento ? '/' + __fieldname__.complemento : '') + ', ' + __fieldname__.bairro + ', ' + __fieldname__.localidade + '/' + __fieldname__.uf + '. CEP: ' + __fieldname__.cep")
	@FieldSet(caption = "Atividades")
	@Edit()
	private List<XAtividade> atividade = new ArrayList<>();

//	@Search
//	@NotNull
//	@Edit(colM = 3)
//	CaeTematica tematica;

//	@Search
//	@NotNull
//	@Edit(caption = "Atividade", colM = 3)
//	CaeAtividade atividade;

	@Search
	@FieldSet(caption = "Datas")
	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeAberturaDasInscricoes;

	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeEncerramentoDasInscricoes;

	@NotNull
	@Edit(caption = "Carga Horária Total", colM = 3)
	int cargaHoraria;

	@FieldSet(caption = "Avaliação de Desempenho")
	@NotNull
	@Edit(caption = "Tipo de Avaliação", colM = 3)
	CaeTipoDeAvaliacaoDeDesempenhoEnum avaliacao;

	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Observações sobre a Avaliação", kind = EditKindEnum.TEXTAREA, colM = 12)
	String obsDaAvaliacao;

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
	@Edit(caption = "Publicar Curso", colM = 4)
	boolean publicar;
	@Edit(caption = "Cancelado", colM = 4)
	boolean cancelada;

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
	}

}
