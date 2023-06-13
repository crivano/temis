package br.jus.trf2.temis.pjd.model;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import org.joda.money.Money;

import com.crivano.jbiz.ITag;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;
import com.crivano.juia.annotations.Show;
import com.crivano.juia.biz.IJuiaAction;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.action.Editar;
import br.jus.trf2.temis.core.util.NoSerialization;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.pjd.model.enm.TipoDeParteEnum;
import br.jus.trf2.temis.pjd.model.event.PeticaoInicialEventoAnotacao;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
@Global(singular = "Petição Inicial", plural = "Petições Iniciais", gender = Gender.SHE, locator = "pro-peticao-inicial", codePrefix = "PI", deletable = true)
public class PeticaoInicial extends Entidade {
//	Endereçamento
//	Identificação do autor (nome, nacionalidade, estado civil, RG, CPF e residência)
//	Classe da ação (ou seria o assunto? O exemplo é: Ação de Obrigação de Fazer C/C Danos Morais)
//	Identificação do réu
//	Fatos
//	Do Dano
//	Do Direito
//	Jurisprudência
//	Do Pedido
//	Provas
//	Valor da Causa
//	Fecho (Pelo deferimento e data)
//	Assinatura (nome e OAB)

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Parte", plural = "Partes", gender = Gender.SHE, locator = "pro-peticao-inicial-x-pessoa", codePrefix = "PIP", deletable = true)
	public static class XPessoa extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Search
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Tipo da Parte", colM = 4)
		@Enumerated(EnumType.STRING)
		TipoDeParteEnum tipoDeParte;

		@Search
		@Show
		@Edit(caption = "Pessoa", colM = 8)
		@ManyToOne(fetch = FetchType.LAZY)
		Pessoa pessoa;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Fato", plural = "Fatos", gender = Gender.SHE, locator = "pro-peticao-inicial-x-fato", codePrefix = "PIF", deletable = true)
	public static class XFato extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Título", colM = 12)
		String titulo;

		@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA, colM = 12)
		String texto;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Dano", plural = "Danos", gender = Gender.SHE, locator = "pro-peticao-inicial-x-dano", codePrefix = "PIDA", deletable = true)
	public static class XDano extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Título", colM = 12)
		String titulo;

		@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA, colM = 12)
		String texto;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Direito", plural = "Direito", gender = Gender.SHE, locator = "pro-peticao-inicial-x-direito", codePrefix = "PIDI", deletable = true)
	public static class XDireito extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Lei", colM = 4)
		String lei;
		@Edit(caption = "Artigo", colM = 2)
		String artigo;
		@Edit(caption = "Parágrafo", colM = 2)
		String paragrafo;
		@Edit(caption = "Alínea", colM = 2)
		String alinea;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Jurisprudência", plural = "Jurisprudências", gender = Gender.SHE, locator = "pro-peticao-inicial-x-jurisprudencia", codePrefix = "PIJU", deletable = true)
	public static class XJurisprudencia extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Título", colM = 12)
		String titulo;

		@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA, colM = 12)
		String texto;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Pedido", plural = "Pedidos", gender = Gender.SHE, locator = "pro-peticao-inicial-x-pedido", codePrefix = "PIPE", deletable = true)
	public static class XPedido extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA, colM = 12)
		String texto;
	}

	@Entity
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Prova", plural = "Provas", gender = Gender.SHE, locator = "pro-peticao-inicial-x-prova", codePrefix = "PIPR", deletable = true)
	public static class XProva extends Entidade {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		private Long id;

		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;

		@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA, colM = 12)
		String texto;
	}

	@Entity
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
	@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING)
	@Data
	@NoArgsConstructor
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Evento", plural = "Eventos", gender = Gender.HE, codePrefix = "PIE")
	public abstract static class XEvento extends Evento<PeticaoInicial, XEvento> {
		@ManyToOne(fetch = FetchType.LAZY)
		private PeticaoInicial peticaoInicial;
	}

//	public static class XAssunto {
//		@OneToMany
//		Assunto assunto;
//	}
//
//	public static class XPrioridade {
//		PrioridadeEnum prioridade;
//	}
//
//	public static class XDocumento {
//		TipoDeDocumentoEnum tipoDeDocumentoEnum;
//		String descrição;
//		String numero;
//		boolean sigiloso;
//	}
//
//	@OneToMany
//	ClasseJudicial classeJudicial;
//	LocalDateTime autuação;
//	LocalDateTime ultimaDistribuicao;

//	boolean segredoDeJustica;
//	boolean justicaGratuita;
//	boolean tutelaOuLiminar;
//	@OneToMany
//	OrgaoJulgador orgaoJulgadorColegiado;
//	@OneToMany
//	OrgaoJulgador orgaoJulgador;
//	@OneToMany
//	Pessoa magistradoRelator;
//	@OneToMany
//	Competencia competencia;

//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	private Long id;

	@OneToMany(mappedBy = XPessoa.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Partes")
	private List<XPessoa> xPessoa;

	@OneToMany(mappedBy = XFato.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Dos Fatos")
	private List<XFato> xFato;

	@OneToMany(mappedBy = XDano.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Dos Danos")
	private List<XDano> xDano;

	@OneToMany(mappedBy = XDireito.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Do Direito")
	private List<XDireito> xDireito;

	@OneToMany(mappedBy = XJurisprudencia.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Da Jurisprudência")
	private List<XJurisprudencia> xJurisprudencia;

	@OneToMany(mappedBy = XFato.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Dos Pedidos")
	private List<XPedido> xPedido;

	@OneToMany(mappedBy = XProva.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@Show
	@Edit()
	@FieldSet(caption = "Das Provas")
	private List<XProva> xProva;

	@NoSerialization
	@OneToMany(mappedBy = XEvento.Fields.peticaoInicial, cascade = CascadeType.ALL)
	@OrderBy(Evento.Fields.dtIni)
	private SortedSet<XEvento> evento = new TreeSet<>();

	@Search
	@Edit(caption = "Valor da Causa", colM = 4, colL = 3)
	Money valorDaCausa;

	@Edit(caption = "Segredo de Justiça", colM = 4, colL = 3)
	boolean segredoDeJustica;
	@Edit(caption = "Justiça Gratuita", colM = 4, colL = 3)
	boolean justicaGratuita;
	@Edit(caption = "Tutela ou Liminar", colM = 4, colL = 3)
	boolean tutelaOuLiminar;

	// List<XAssunto> xAssunto;
//	List<XPrioridade> xPrioridade;

	@Override
	public void addActions(SortedSet<IJuiaAction> set) {
		super.addActions(set);
		set.add(new Editar());
		set.add(new PeticaoInicialEventoAnotacao());
	}

	@Override
	public void addTags(SortedSet<ITag> set) {
		super.addTags(set);

//		if (true)
//			set.add(Etiqueta.of(null, this, getPessoaCadastrante(), getUnidadeCadastrante(),
//					MarcadorEnum.PENDENTE_DE_ASSINATURA, this.getBegin(), null));
	}

}
