package br.jus.trf2.temis.cae.model;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Menu;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.annotations.ShowGroup;

import br.jus.trf2.temis.core.Entidade;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
@Menu(create = true)
@Global(singular = "Tipo de Atividade", plural = "Tipos de Atividades", gender = Gender.HE, locator = "cae-tipo-de-atividade", codePrefix = "TA", deletable = true)
public class CaeTipoDeAtividade extends Entidade {
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
	public String getDescrCompleta() {
		return descricao;

	}

	@Override
	public String getSelectFirstLine() {
		return descricao;

	}
}
