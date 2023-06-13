package br.jus.trf2.temis.pjd.model.event;

import java.util.HashSet;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.crivano.jlogic.Expression;
import com.crivano.jlogic.Not;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.core.enm.TipoDePessoaJuridicaEnum;
import br.jus.trf2.temis.core.util.ContextInterceptor;
import br.jus.trf2.temis.core.util.Dao;
import br.jus.trf2.temis.core.util.DescrBuilder;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.core.util.ModeloUtils;
import br.jus.trf2.temis.iam.model.Agente;
import br.jus.trf2.temis.iam.model.Endereco;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.enm.TipoDePessoaEnum;
import br.jus.trf2.temis.iam.model.enm.UfEnum;
import br.jus.trf2.temis.pjd.logic.ParteDeveIncluirDocumentoComprobatorioDeEPP;
import br.jus.trf2.temis.pjd.logic.ParteDeveIncluirFontePagadora;
import br.jus.trf2.temis.pjd.logic.PartePodeIncluirFontePagadora;
import br.jus.trf2.temis.pjd.logic.ProcessoTemAutorEReu;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.Processo.EventoProcessual;
import br.jus.trf2.temis.pjd.model.enm.TipoDeParteEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Parte", plural = "Inclusões de Partes", gender = Gender.SHE, action = "Incluir Parte", icon = "fas fa-plus")
@FieldNameConstants
public class EventoProcessualInclusaoDeParte extends Processo.EventoProcessual {
	@Edit(caption = "Tipo da Parte", colM = 4)
	@Required
	@Enumerated(EnumType.STRING)
	TipoDeParteEnum tipoDeParte;

	@Transient
	@Required
	@Edit(caption = "Tipo da Pessoa", colM = 4)
	@Enumerated(EnumType.STRING)
	private TipoDePessoaEnum tipoPessoa = TipoDePessoaEnum.PESSOA_FISICA;

	@Transient
	@Required
	@Edit(caption = "{{act." + Fields.tipoPessoa + " == 'PESSOA_JURIDICA' ? 'CNPJ': 'CPF'}}", colM = 4, attr = {
			":mask=act." + Fields.tipoPessoa + " == 'PESSOA_JURIDICA' ? '##.###.###/####-##': '###.###.###-##'",
			":validate=act." + Fields.tipoPessoa
					+ " == 'PESSOA_JURIDICA' ? 'cnpj': 'cpf'" }, attrContainer = "v-if=act." + Fields.tipoPessoa
							+ " != 'ENTIDADE'")
	@Column(length = 20)
	private String cpfcnpj;

	@Transient
	@Required
	@Edit(caption = "Tipo da Pessoa Jurídica", colM = 12, attrContainer = "v-if=act." + Fields.tipoPessoa
			+ " == 'PESSOA_JURIDICA' && act." + Fields.tipoDeParte + " == 'ATIVO_PARTE'")
	@Enumerated(EnumType.STRING)
	private TipoDePessoaJuridicaEnum tipoPessoaJuridica;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "{{act." + Fields.tipoPessoa
			+ " == 'PESSOA_JURIDICA' ? 'Nome Fantasia': 'Nome'}}", colM = 6, attrContainer = "v-if=act."
					+ Fields.tipoPessoa + " != 'ENTIDADE'")
	@Column(length = 128)
	private String nome;

	@Transient
	@Required
	@Column(length = 128)
	@Edit(caption = "Razão Social", colM = 6, attrContainer = "v-if=act." + Fields.tipoPessoa + " == 'PESSOA_JURIDICA'")
	private String razaoSocial;

	@Transient
	@Required
	@ManyToOne
	@Edit(caption = "Entidade", colM = 12, attrContainer = "v-if=act." + Fields.tipoPessoa + " == 'ENTIDADE'")
	private Pessoa entidade;

	// @Embedded
//	@FieldSet(caption = "RG", attr = "ng-show=ng-show=data.tipoPessoa == 'PESSOA_FISICA'")
//	@Edit()
//	private Rg rg;

	@Transient
	@Required
	@Column(length = 20)
	@Edit(caption = "Celular", colM = 3, attr = ":mask='(##) #####-####'", attrContainer = "v-if=act."
			+ Fields.tipoPessoa + " != 'ENTIDADE'")
	private String celular;

	@Transient
	@Column(length = 20)
	@Edit(caption = "Telefone", colM = 3, attr = ":mask='(##) ####-####'", attrContainer = "v-if=act."
			+ Fields.tipoPessoa + " != 'ENTIDADE'")
	private String telefone;

	@Transient
	@Required
	@Column(length = 40)
	@Edit(caption = "Email Principal", colM = 3, attr = "validate=email", attrContainer = "v-if=act."
			+ Fields.tipoPessoa + " != 'ENTIDADE'")
	private String email;

	@Transient
	@Required
	@NonNull
	@FieldSet(caption = "Endereço", attr = "v-if=act." + Fields.tipoPessoa + " != 'ENTIDADE'")
	@Search
	@Edit(caption = "CEP", colM = 3, attr = ":mask='#####-###'")
	@Column(length = 9)
	private String cep;

	@Transient
	@Required
	@NonNull
	@Search
	@Edit(caption = "Logradouro", colM = 4)
	@Column(length = 40)
	private String logradouro;

	@Transient
	@Required
	@NonNull
	@Search
	@Edit(caption = "Número", colM = 2)
	@Column(length = 10)
	private String numero;

	@Transient
	@Search
	@Edit(caption = "Complemento", colM = 3)
	@Column(length = 20)
	private String complemento;

	@Transient
	@Required
	@NonNull
	@Search
	@Edit(caption = "Bairro", colM = 3)
	@Column(length = 40)
	private String bairro;

	@Transient
	@Required
	@NonNull
	@Search
	@Edit(caption = "Localidade", colM = 4)
	@Column(length = 40)
	private String localidade;

	@Transient
	@Required
	@NonNull
	@Search
	@Edit(caption = "UF", colS = 4, colM = 2)
	@Enumerated(EnumType.STRING)
	private UfEnum uf;

	@FullSerialization
	@NonNull
	@ManyToOne
	Pessoa pessoa;

	@FullSerialization
	@NonNull
	@ManyToOne
	Endereco endereco;

//	@Edit(caption = "Pessoa", colM = 8)
//	@ManyToOne(fetch = FetchType.LAZY)
//	Pessoa pessoa;

	@Override
	public String getDescr() {
		return DescrBuilder.builder().add("", pessoa.getNome()).add("", pessoa.getDescrCompleta())
				.add("endereço: ", endereco != null ? endereco.getDescrCompleta() : null).build();
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
		set.add(new EventoProcessualInclusaoDeDocumentoGenerico());
		set.add(new EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp());
		set.add(new EventoProcessualInclusaoDeFontePagadora());
	}

	@Override
	public void execute(Agente actor, Agente onBehalfOf, Processo element, EventoProcessual evento, Etiqueta etiqueta)
			throws Exception {
		if (this.tipoPessoa == TipoDePessoaEnum.ENTIDADE && this.entidade != null) {
			this.pessoa = this.entidade;
			super.execute(actor, onBehalfOf, element, evento, etiqueta);
			return;
		}

		Dao dao = ContextInterceptor.getDao();
		HashSet<String> exceto = new HashSet<String>();
		exceto.add("id");
		Pessoa pessoa = null;
		Endereco endereco = null;

		// Reaproveitar pessoa
		if (cpfcnpj != null) {
			for (Pessoa i : dao.listarPessoaPorCpfCnpj(cpfcnpj)) {
				if (ModeloUtils.compareProperties(i, this, exceto)) {
					pessoa = i;
					break;
				}
			}
		}

		// Reaproveitar endereço
		if (pessoa != null) {
			for (Endereco i : dao.listarEndercoPorIdPessoa(pessoa)) {
				if (ModeloUtils.compareProperties(i, this, exceto)) {
					endereco = i;
					break;
				}
			}
		}

		if (pessoa == null) {
			pessoa = new Pessoa();
			ModeloUtils.copyProperties(pessoa, this, exceto);
			dao.persist(pessoa);
		}
		this.pessoa = pessoa;

		if (endereco == null) {
			endereco = new Endereco();
			ModeloUtils.copyProperties(endereco, this, exceto);
			dao.persist(endereco);
		}
		this.endereco = endereco;

		super.execute(actor, onBehalfOf, element, evento, etiqueta);
	}

	public boolean isAutor() {
		return getTipoDeParte() == TipoDeParteEnum.ATIVO_PARTE;
	}

	public boolean isReu() {
		return getTipoDeParte() == TipoDeParteEnum.PASSIVO_PARTE;
	}

	public boolean isEmpresa() {
		return pessoa.getTipoPessoa() == TipoDePessoaEnum.PESSOA_JURIDICA;
	}

	public boolean isEmpresaDePequenoPorte() {
		return isEmpresa() && (pessoa.getTipoPessoaJuridica() == TipoDePessoaJuridicaEnum.MEI
				|| pessoa.getTipoPessoaJuridica() == TipoDePessoaJuridicaEnum.LTDA_EPP);
	}

	public boolean isContemDocumentoComprobatorioDeEpp() {
		SortedSet<EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp> set = getEventos(
				EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp.class);
		return set.size() > 0;
	}

	public boolean isContemFontePagadora() {
		SortedSet<EventoProcessualInclusaoDeFontePagadora> set = getEventos(
				EventoProcessualInclusaoDeFontePagadora.class);
		return set.size() > 0;
	}

	public <T extends EventoProcessual> SortedSet<T> getEventos(Class<T> clazz) {
		SortedSet<T> set = getProcesso().getEventos(clazz);
		set.removeIf(e -> {
			boolean equal = this.getId().equals(e.getReferente().getId());
			System.out.println(this.getId() + " - " + e.getReferente().getId() + " - " + equal);
			return !equal;
		});
		return set;
	}

	@Override
	public Expression getActiveMiniAction(Agente actor, Agente onBehalfOf, Processo element, Acao miniAction) {
		if (miniAction instanceof EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp)
			return ParteDeveIncluirDocumentoComprobatorioDeEPP.of(element, this);
		if (miniAction instanceof EventoProcessualInclusaoDeFontePagadora)
			return PartePodeIncluirFontePagadora.of(element, this);
		return super.getActiveMiniAction(actor, onBehalfOf, element, miniAction);
	}

	public Expression getRequiredMiniAction(Agente actor, Agente onBehalfOf, Processo element, Acao miniAction) {
		if (miniAction instanceof EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp)
			return ParteDeveIncluirDocumentoComprobatorioDeEPP.of(element, this);
		if (miniAction instanceof EventoProcessualInclusaoDeFontePagadora)
			return ParteDeveIncluirFontePagadora.of(element, this);
		return super.getRequiredMiniAction(actor, onBehalfOf, element, miniAction);
	}

	@Override
	public String getTitulo() {
		return pessoa.getNomeCompacto() + " (" + tipoDeParte.getNome() + ")";
	}

	@Override
	public Expression getRequired(Processo processo, EventoProcessual event) {
		return Not.of(ProcessoTemAutorEReu.of(processo));
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}
}
