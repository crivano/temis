package br.jus.trf2.temis.pjd.model.event;

import javax.persistence.Entity;

import org.joda.money.Money;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.pjd.model.Processo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Definição de Valor da Causa", plural = "Definições de Valor da Causa", gender = Gender.SHE, action = "Definir Valor da Causa", icon = "fas fa-pencil-alt")
public class EventoProcessualDefinicaoDeValorDaCausa extends Processo.EventoProcessual {
	@Required
	@NonNull
	@Edit(caption = "Valor da Causa", colM = 12)
	Money valor;

	@Override
	public String getDescr() {
		return valor.toString();
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}
}
