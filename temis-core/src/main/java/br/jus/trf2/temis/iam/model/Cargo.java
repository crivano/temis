package br.jus.trf2.temis.iam.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.util.Utils;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Cargo", plural = "Cargos", gender = Gender.HE, locator = "iam-cargo")
public class Cargo extends Entidade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Search(caption = "Sigla")
	@Edit(caption = "Sigla", colM = 6)
	private String sigla;

	@Search
	@Edit(caption = "Nome", colM = 6)
	private String nome;

	public String getSelectFirstLine() {
		return Utils.concat(sigla, nome);
	}

	public String getSelectSecondLine() {
		return null;
	}

	@Override
	public String getCode() {
		return this.sigla;
	}

	@Override
	public String getDescr() {
		return this.getNome();
	}
}
