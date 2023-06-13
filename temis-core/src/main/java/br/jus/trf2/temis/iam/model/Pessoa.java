package br.jus.trf2.temis.iam.model;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.validation.constraints.NotNull;

import com.crivano.jsync.IgnoreForDependecyLevel;
import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.juia.annotations.Detail;
import com.crivano.juia.annotations.DetailGroup;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.annotations.ShowGroup;
import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.action.Editar;
import br.jus.trf2.temis.core.enm.TipoDePessoaJuridicaEnum;
import br.jus.trf2.temis.core.util.DescrBuilder;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.enm.TipoDePessoaEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@FieldNameConstants
@Global(singular = "Pessoa", plural = "Pessoas", gender = Gender.SHE, locator = "iam-pessoa", versionable = true)
public class Pessoa extends Entidade implements Comparable<Pessoa> {
	private Integer sequencial;

	// @Index
	// private LocalDateTime dataHora;
	//
	// private LocalDateTime dataHoraEntrega;

	@DetailGroup(caption = "Informações")
	@Detail
	@Edit(caption = "Tipo da Pessoa", colM = 3)
	@Enumerated(EnumType.STRING)
	private TipoDePessoaEnum tipoPessoa = TipoDePessoaEnum.PESSOA_FISICA;

	@Detail
	@Edit(caption = "Tipo de Pessoa Jurídica", colM = 3)
	@Enumerated(EnumType.STRING)
	private TipoDePessoaJuridicaEnum tipoPessoaJuridica;

	// @Edit(caption = "Tipo de Colaborador", colM = 3)
	// private TipoDeColaboradorEnum tipoColaborador =
	// TipoDeColaboradorEnum.FUNCIONARIO;
	//
	// @Show
	// @Edit(colM = 6)
	// private @OneToMany Cargo> cargo;

	@Detail
	@Edit(caption = "Sigla", colM = 3)
	private String sigla;

	@Detail
	@Edit(caption = "Apelido", colM = 3)
	private String apelido;

	@ShowGroup(caption = "{{data.nome}}", colXS = 12, colM = 6)
	@Show(caption = "CPF/CNPJ")
	@Search(caption = "CPF/CNPJ")
	@Edit(caption = "CPF/CNPJ", colM = 3, attr = { "ui-br-cpfcnpj-mask",
			"ng-disabled=!(usuario.admin || usuario.agentePapel === 'PROPRIETARIO' || usuario.agentePapel === 'ADMINISTRADOR') &amp;&amp; data.id &amp;&amp; data.cpfcnpj" })
	private String cpfcnpj;

	@NotNull
	@Search
	@Show
	@Edit(caption = "Nome/Nome Fantasia", colM = 6)
	private String nome;

	@Show
	@Edit(caption = "Razão Social", colM = 6, attrContainer = "ng-show=data.tipoPessoa == 'PESSOA_JURIDICA'")
	private String razaoSocial;

//	@Embedded
//	@FieldSet(caption = "RG", attr = "ng-show=ng-show=data.tipoPessoa == 'PESSOA_FISICA'")
//	@Edit()
//	private Rg rg;

	@Show
	@Edit(caption = "Celular", colM = 3, attr = "ui-br-phone-number-mask")
	@Search
	private String celular;

	@Show
	@Edit(caption = "Telefone", colM = 3, attr = "ui-br-phone-number-mask")
	@Search
	private String telefone;

	@Show
	@Edit(caption = "Email Principal", colM = 3)
	@Search
	private String email;

	@Show
	@Edit(caption = "Registro Geral", colM = 3)
	@Search
	private String rg;

	@Edit(caption = "Informar Outro Endereço para Entrega", colM = 6)
	private boolean enderecoEntregaDiferente;

	@Edit(caption = "Informar Outro Endereço para Cobrança", colM = 6)
	private boolean enderecoCobrancaDiferente;

	// transportadoraPadrão;

	// Dados adicionais
	// grupo

	// @Detail
	// @FieldSet(caption = "Dados Adicionais")
	// @Edit(caption = "Inscrição Estadual", colM = 4, attr =
	// "ui-br-ie-mask=data.endereco.UF")
	// private String inscricaoEstadual;
	//
	// @Detail
	// @Edit(caption = "Data de Nascimento/Fundação", colM = 4, attr =
	// "ui-date-mask")
	// private LocalDate dtNascimentoOuFundacao;
	//
	// @Detail
	// @Edit(caption = "Data de Cadastro", colM = 4, attr = "ui-date-mask")
	// private LocalDate dtCadastro;
	//
	// @Detail
	// @Edit(caption = "Email do Faturamento", colM = 4)
	// private String emailSetorFaturamento;
	//
	// @Detail
	// @Edit(caption = "Email do Comercial", colM = 4)
	// private String emailSetorComercial;
	//
	// @Detail
	// @Edit(caption = "Website", colM = 4)
	// private String site;

	// faixaDeFuncionarios

	// Ramo

	@Search
	@FieldSet(caption = "Observações")
	@Edit(caption = "Observações")
	private String observacoes;

	// Endereços
	@IgnoreForDependecyLevel
	@IgnoreForSimilarity
	@OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL)
	@Show(caption = "Principal", value = "__fieldname__.logradouro + ' ' + __fieldname__.numero + (__fieldname__.complemento ? '/' + __fieldname__.complemento : '') + ', ' + __fieldname__.bairro + ', ' + __fieldname__.localidade + '/' + __fieldname__.uf + '. CEP: ' + __fieldname__.cep")
	@FieldSet(caption = "Endereços")
	@Edit()
	private List<Endereco> endereco = new ArrayList<>();

	// Dados Financeiros
	// @Detail
	// @FieldSet(caption = "Dados Financeiros")
	// @Edit(caption = "Limite de Crédito", colM = 4)
	// private Money limiteDeCredito;
	// @Detail
	// @Edit(caption = "Periodicidade de Compra e Venda", colM = 4)
	// private Integer periodicidadeDeVendaECompra;
	// @Detail
	// @Edit(caption = "Valor Mínimo de Compra", colM = 4)
	// private Money valorMinimoDeCompra;

	// private tabelaDePreçoPadrao

	// Contas Bancárias - deveria ser possível cadastrar várias
	// @Detail
	// @FieldSet(caption = "Conta Bancária")
	// @Edit(caption = "Banco", colM = 4)
	// private BancoEnum contaBancariaBanco;
	// @Detail
	// @Edit(caption = "Agência", colM = 4)
	// private String contaBancariaAgencia;
	// @Detail
	// @Edit(caption = "Conta-Corrente", colM = 4)
	// private String contaBancariaNumero;

	// @FieldSet(caption = "Conta Bancária")
	// @Edit(caption = "Observações")
	// private String Observacoes;

	public String getSelectFirstLine() {
		return Utils.concat(cpfcnpj, nome);
	}

	public String getSelectSecondLine() {
		return Utils.concat(email, celular, telefone);
	}

	@Override
	public String getCode() {
		return this.cpfcnpj != null ? this.cpfcnpj : this.getId().toString();
	}

	@Override
	public String getDescr() {
		return this.getNome();
	}

	@Override
	public String getDescrCompleta() {
		return DescrBuilder.builder().add("CPF/CNPJ: ", cpfcnpj).add("Celular: ", celular).add("Telefone: ", telefone)
				.add("Email: ", email).build();
	}

	public String getNomeCompacto() {
		if (apelido != null)
			return apelido;
		if (sigla != null)
			return sigla;
		return nome;
	}

	@PrePersist
	public void prePersist() {
		// super.fixMappedBy();
	}

	@PreUpdate
	public void preUpdate() {
		// super.fixMappedBy();
	}

	@Override
	public int compareTo(Pessoa o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		set.add(new Editar());
	}
}
