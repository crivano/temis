package br.jus.trf2.temis.cae.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.crivano.jbiz.ITag;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Menu;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.annotations.ShowGroup;
import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.action.Auditar;
import br.jus.trf2.temis.core.action.Editar;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.crp.model.CrpLotacao;
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
@Global(singular = "Instituição de Ensino", plural = "Instituições de Ensino", gender = Gender.SHE, locator = "cae-instituicao-de-ensino", codePrefix = "IE", deletable = true)
public class CaeInstituicaoDeEnsino extends Entidade {
	@Search
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Nome", colM = 3)
	String nome;

	@Search
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Descrição", colM = 3)
	String descricao;

	@Override
	public String getDescr() {
		return descricao;
	}

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		// set.add(new Editar<Processo>());
		set.add(new Editar());
		set.add(new Auditar());
	}

	@Override
	public void addTags(SortedSet<ITag> set) {
		super.addTags(set);

//		if (!isAutuado())
//			set.add(Etiqueta.of(null, this, null, getPessoaCadastrante(), getUnidadeCadastrante(),
//					MarcadorEnum.EM_ELABORACAO, this.getBegin(), null));
//		if (isAutuado())
//			set.add(Etiqueta.of(null, this, null, getPessoaCadastrante(), getUnidadeCadastrante(),
//					MarcadorEnum.AGUARDANDO_DECISAO, this.getBegin(), null));
	}

}
