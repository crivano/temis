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

import com.crivano.jsync.IgnoreForDependecyLevel;
import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Menu;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.annotations.ShowGroup;
import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.cae.model.enm.CaeMultiplicadorDeConvolacaoEnum;
import br.jus.trf2.temis.cae.model.enm.CaeTipoDeAtividadeEnum;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.action.Auditar;
import br.jus.trf2.temis.core.action.Editar;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.crp.model.CrpPessoa;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
@Menu(list = true)
@Global(singular = "Convolação", plural = "Convolações", gender = Gender.SHE, locator = "cae-convolacao", codePrefix = "CN", deletable = true)
public class CaeConvolacao extends Entidade {

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Atividade", plural = "Atividades", gender = Gender.SHE, locator = "pro-convolacao-x-atividade", codePrefix = "CA", deletable = true)
	public static class XTipoDeAtividade extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private CaeConvolacao convolacao;

		@NotNull
		@Edit(colM = 3)
		CaeMultiplicadorDeConvolacaoEnum multiplicador;

		@NotNull
		@Edit(caption = "Tipo", colM = 6)
		CaeTipoDeAtividadeEnum tipo;

		@Edit(caption = "Observações", colM = 3)
		String obs;
	}

	@Edit(caption = "Magistrado", colM = 6)
	@NotNull
	@ManyToOne
	CrpPessoa magistrado;

	@Search
	@NotNull
	@Edit(colM = 3)
	int ano;

	@Search
	@Show
	@NotNull
	@Edit(colM = 8)
	@ManyToOne(fetch = FetchType.LAZY)
	CaeAtividade atividade;

	@Edit(colM = 6)
	boolean anularValorDaAtividade;

	@IgnoreForDependecyLevel
	@IgnoreForSimilarity
	@OneToMany(mappedBy = "convolacao", cascade = CascadeType.ALL)
	@Show(caption = "Tipos de Atividades", value = "__fieldname__.logradouro + ' ' + __fieldname__.numero + (__fieldname__.complemento ? '/' + __fieldname__.complemento : '') + ', ' + __fieldname__.bairro + ', ' + __fieldname__.localidade + '/' + __fieldname__.uf + '. CEP: ' + __fieldname__.cep")
	@FieldSet(caption = "Tipos de Atividades")
	@Edit()
	private List<XTipoDeAtividade> tipoDeAtividade = new ArrayList<>();

	@Override
	public String getDescr() {
		return magistrado == null ? null : magistrado.getNome();
	}

	@Override
	public String getDescrCompleta() {
		return getDescr();
	}

	@Override
	public String getSelectFirstLine() {
		return getDescr();
	}

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		// set.add(new Editar<Processo>());
		set.add(new Editar());
		set.add(new Auditar());
	}

}
