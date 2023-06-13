package br.jus.trf2.temis.core.action;

import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.iam.model.Agente;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
@Global(singular = "Exibição de PDF", plural = "Exibições de PDF", gender = Gender.SHE, action = "Exibir PDF", icon = "far fa-file-pdf")
public class ExibirPdf<E extends Entidade, T extends Evento<E, T>> implements Acao<E, Agente, T> {
	Evento evento;

	@Override
	public String getClick(E entidade, T ev) {
		if (evento != null) {
//			return "this.showPdf(" + evento.getArquivo().getId() + ")";
		}
		return "this.showPdf()";
	}
}
