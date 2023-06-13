package br.jus.trf2.temis.iam.model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.enm.TipoDeColaboradorEnum;
import br.jus.trf2.temis.iam.model.enm.TipoPapelEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Papel", plural = "Papéis", gender = Gender.HE, locator = "iam-papel")
public class Papel extends Entidade {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Edit(colM = 3)
	@Enumerated(EnumType.STRING)
	private TipoPapelEnum tipoPapel;

	@Edit(caption = "Tipo de Colaborador", colM = 3)
	@Enumerated(EnumType.STRING)
	private TipoDeColaboradorEnum tipoColaborador = TipoDeColaboradorEnum.FUNCIONARIO;

	@Search
	@Edit(caption = "Pessoa", colM = 6)
	@NotNull
	private Pessoa pessoa;

	@Search
	@Edit(caption = "Unidade", colM = 6)
	@NotNull
	private Unidade unidade;

	@Search
	@Edit(colM = 6)
	private Cargo cargo;

	@Search
	@Edit(caption = "Função de Confiança", colM = 6)
	private FuncaoDeConfianca funcaoDeConfianca;

	@FieldSet(caption = "Detalhes do Papel (opcional)")
	@Edit(caption = "Sigla", colM = 6)
	private String sigla;

	@Edit(caption = "Nome do papel", colM = 6)
	private String nome;

	@Edit(caption = "Observações", colM = 12)
	private String obs;

	@FieldSet(caption = "Reformatar Assinatura (opcional)")
	@Edit(caption = "Nome", colM = 6)
	private String assinaturaNome;

	@Edit(caption = "Cargo/Função", colM = 6)
	private String assinaturaCargo;

	@Edit(caption = "Função de Confiança", colM = 6)
	private String assinaturaFuncao;

	@Edit(caption = "Unidade", colM = 6)
	private String assinaturaUnidade;

	public String getSelectFirstLine() {
		String s = "";
		if (pessoa != null)
			s += pessoa.getNome() + " - ";
		s += getCode();
		return s;
	}

	public String getSelectSecondLine() {
		return getDescr();
	}

	@Override
	public String getCode() {
		return Utils.concat(funcaoDeConfianca != null ? funcaoDeConfianca.getSigla() : "", unidade.getSigla());
	}

	@Override
	public String getDescr() {
		return getNomeCompacto();
	}

	public String getNomeCompacto() {
		return Utils.concat(funcaoDeConfianca != null ? funcaoDeConfianca.getSigla() : "",
				funcaoDeConfianca == null && pessoa != null ? pessoa.getNomeCompacto() : "", unidade.getSigla());
	}

}
