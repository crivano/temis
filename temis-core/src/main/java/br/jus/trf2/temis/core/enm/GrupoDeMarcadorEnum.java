package br.jus.trf2.temis.core.enm;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Global(singular = "Banco", plural = "Bancos", gender = Gender.HE)
@AllArgsConstructor
@Getter
public enum GrupoDeMarcadorEnum implements IEnum {
	ALERTA("Alertas", "hourglass-end"),
	//
	CAIXA_DE_ENTRADA("Caixa de Entrada", "inbox"),
	//
	AGUARDANDO_PAGAMENTO("Aguardando Pagamento", "comment-dollar"),
	//
	AGUARDANDO_ANDAMENTO("Aguardando Andamento", "lightbulb"),
	//
	CAIXA_DE_SAIDA("Caixa de Saída", "inbox"),
	//
	ACOMPANHANDO("Acompanhando", "key"),
	//
	MONITORANDO("Monitorando", "hourglass-half"),
	//
	OUTROS("Outros", "paper-plane"),
	//
	QUALQUER("Qualquer", "inbox"),
	//
	NENHUM("Nenhum", "inbox");

	private final String nome;
	private final String icone;

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
