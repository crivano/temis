package br.jus.trf2.temis.iam.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.util.DescrBuilder;
import br.jus.trf2.temis.iam.model.enm.UfEnum;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@FieldNameConstants
@Global(singular = "Endereço", plural = "Endereços", gender = Gender.HE, locator = "iam-endereco", sortField = Endereco.Fields.ordem, versionable = true)
public class Endereco extends Entidade {
	@ManyToOne(fetch = FetchType.LAZY)
	private Pessoa pessoa;

	@Search
	@Edit(caption = "CEP", colS = 4, colM = 2, attr = "ui-br-cep-mask")
	private String cep;
	@Search
	@Edit(caption = "Logradouro", colS = 8, colM = 4)
	private String logradouro;
	@Search
	@Edit(caption = "Número", colS = 4, colM = 2)
	private String numero;
	@Search
	@Edit(caption = "Complemento", colS = 8, colM = 4)
	private String complemento;
	@Search
	@Edit(caption = "Bairro", colM = 3)
	private String bairro;
	@Search
	@Edit(caption = "Localidade", colM = 3)
	private String localidade;
	@Search
	@Edit(caption = "UF", colS = 4, colM = 2)
	@Enumerated(EnumType.STRING)
	private UfEnum uf;
	// @Edit(caption = "Código da Cidade")
	// private String codigoCidade;
	// @Edit(caption = "País")
	// private String pais;
	// @Edit(caption = "Código do País")
	// private String codigoPais;
	@Edit(caption = "URL do Google Maps", colS = 8, colM = 4, attrContainer = "ng-hide=externo")
	private String urlLocalizacaoNoMapa;

	private Integer ordem;

	public String toString() {
		return getDescrCompleta();
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescr() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescrCompleta() {
		return DescrBuilder.builder().add("", logradouro).add(" ", numero).add("/", "", complemento, "")
				.add(" - ", "", bairro, "").add(" - ", "", localidade, "")
				.add("/", "", uf != null ? uf.name() : null, "").add("CEP: ", cep).build();
	}

}
