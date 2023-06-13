package br.jus.trf2.temis.cae.model;

import java.util.SortedSet;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.FieldSet;
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
@Global(singular = "Comissão Temática", plural = "Comissões Temáticas", gender = Gender.SHE, locator = "cae-tematica", codePrefix = "TM", deletable = true, inactivable = true)
public class CaeTematica extends Entidade {

	@Search
	@ShowGroup(caption = "")
	@Show
	@NotNull
	@Edit(caption = "Nome", colM = 6)
	String nome;

	@Search
	@FieldSet(caption = "Datas")
	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeInicio;

	@NotNull
	@Edit(colM = 3)
	LocalDate dataDeFim;

	@Override
	public String getDescr() {
		return nome;
	}

	@Override
	public String getDescrCompleta() {
		return nome;

	}

	@Override
	public String getSelectFirstLine() {
		return nome;

	}

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		// set.add(new Editar<Processo>());
		set.add(new Editar());
		set.add(new Auditar());
	}

}
