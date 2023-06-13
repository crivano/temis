package br.jus.trf2.temis.core.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Banco", plural = "Bancos", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum MarcadorEnum implements IEnum {
	ALERTA_DE_DATA_LIMITE("Data Limite", "fa fa-hourglass-end", "Data limite atingida", GrupoDeMarcadorEnum.ALERTA,
			true, true, TipoDeExibicaoDeMarcadorEnum.GERAL),
	//
	ALERTA_DE_DATA_PLANEJADA("Data Planejada", "fa fa-hourglass-end", "Data planejada atingida",
			GrupoDeMarcadorEnum.ALERTA, true, true, TipoDeExibicaoDeMarcadorEnum.GERAL),
	//
	A_RECEBER("A Receber", "fa fa-inbox", "Pendente de recebimento por pessoa ou unidade",
			GrupoDeMarcadorEnum.CAIXA_DE_ENTRADA, true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	NOVO("Novo", "far fa-sun", "Criado no sistema", GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO, true, true,
			TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	EM_ELABORACAO("Em Elaboração", "fa fa-lightbulb", "Ainda não autuado", GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO,
			true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	PENDENTE_DE_ASSINATURA("Pendente de Assinatura", "fas fa-key",
			"Ainda não assinado pelo subscritor ou cossignatários", GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO, true,
			true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	A_ASSINAR("A Assinar", "fa fa-key", "Deve ser assinado pelo subscritor ou cossignatário",
			GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO, true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	ASSINADO("Assinado", "fa fa-key", "Recebido e aguardando o próximo andamento",
			GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO, true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	RECEBIDO("Recebido", "fa fa-bomb", "Recebido e aguardando o próximo andamento",
			GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO, true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	AGUARDANDO("Aguardando", "far fa-clock", "Aguardando o próximo andamento", GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO,
			true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	AGUARDANDO_DECISAO("Aguardando Decisão", "far fa-clock", "Aguardando o próximo andamento",
			GrupoDeMarcadorEnum.AGUARDANDO_ANDAMENTO, true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	AGUARDANDO_PAGAMENTO("Aguardando Pagamento", "far fa-money-bill-alt", "Aguardando o próximo andamento",
			GrupoDeMarcadorEnum.AGUARDANDO_PAGAMENTO, true, true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	URGENTE("Urgente", "fa fa-exclamation", "Marcado como urgente", GrupoDeMarcadorEnum.QUALQUER, false, false,
			TipoDeExibicaoDeMarcadorEnum.GERAL),
	//
	PRIORITARIO("Prioritário", "fa fa-flag", "Marcado como prioritário", GrupoDeMarcadorEnum.QUALQUER, false, false,
			TipoDeExibicaoDeMarcadorEnum.GERAL),
	//
	INTERESSADO("Interessado", "fa fa-pushpin", "Com perfil de Interessado", GrupoDeMarcadorEnum.ACOMPANHANDO, true,
			false, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	GESTOR("Gestor", "fa fa-pushpin", "Com perfil de Gestor", GrupoDeMarcadorEnum.ACOMPANHANDO, true, false,
			TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	FISCAL_ADMINISTRATIVO("Fiscal Administrativo", "fa fa-pushpin", "Com perfil de Fiscal Administrativo",
			GrupoDeMarcadorEnum.ACOMPANHANDO, true, false, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	FISCAL_TECNICO("Fiscal Técnico", "fa fa-pushpin", "Com perfil de Fiscal Técnico", GrupoDeMarcadorEnum.ACOMPANHANDO,
			true, false, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	MONITORADO("Monitorado", "fa fa-hourglass-half", "Contando prazo", GrupoDeMarcadorEnum.ACOMPANHANDO, true, true,
			TipoDeExibicaoDeMarcadorEnum.GERAL),
	//
	ENVIADO("Enviado", "fa fa-paper-plane", "Enviado mas ainda não recebido", GrupoDeMarcadorEnum.CAIXA_DE_SAIDA, true,
			true, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	SOBRESTADO("Sobrestado", "fa fa-hourglass-1", "Sobrestado e aguardando", GrupoDeMarcadorEnum.OUTROS, true, true,
			TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	JUNTADO("Juntado", "fa fa-lock", "Juntado a outro documento", GrupoDeMarcadorEnum.NENHUM, false, false,
			TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	BAIXADO("Arquivado", "fa fa-power-off", "Trâmite concluído", GrupoDeMarcadorEnum.NENHUM, false, false,
			TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
//	TRANSFERIDO("Transferido ao Arquivo Intermediário", "fa fa-archive", "Arquivado no arquivo intermediário", GrupoDeMarcadorEnum.NENHUM, false,
//	false, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
//	RECOLHIDO("Recolhido ao Arquivo Permanente", "fa fa-archive", "Arquivado no arquivo permanente", GrupoDeMarcadorEnum.NENHUM, false,
//	false, TipoDeExibicaoDeMarcadorEnum.MARCADOS),
	//
	CANCELADO("Cancelado", "fa fa-ban", "Cancelado e sem efeito", GrupoDeMarcadorEnum.NENHUM, false, false,
			TipoDeExibicaoDeMarcadorEnum.MARCADOS);

	private final String nome;
	private final String icone;
	private final String descricao;
	private final GrupoDeMarcadorEnum grupo;
	private final boolean marcaPessoa;
	private final boolean marcaUnidade;
	private final TipoDeExibicaoDeMarcadorEnum exibicao;

	@Override
	public Long getId() {
		return (long) this.ordinal();
	}

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getDescr() {
		return this.nome;
	}
}
