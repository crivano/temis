package br.jus.trf2.temis.pjd.model.event;

import java.util.HashSet;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.action.ExcluirMiniAction;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.Agente;
import br.jus.trf2.temis.pjd.model.Norma;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.Processo.EventoProcessual;
import br.jus.trf2.temis.pjd.model.enm.DispositivoDeNormaEnum;
import br.jus.trf2.temis.pjd.model.enm.TipoDeNormaEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Norma Jurídica", plural = "Inclusões de Normas Jurídicas", gender = Gender.SHE, action = "Incluir Norma Jurídica", icon = "fas fa-plus")
public class EventoProcessualInclusaoDeNorma extends Processo.EventoProcessual {

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Sigla", colM = 2, attr = "mask=XXXXXXXXXXX")
	String siglaNorma;

	@Transient
	@Edit(caption = "Nome da Norma Jurídica", colM = 10)
	String nomeNorma;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Tipo", colM = 4, colL = 4)
	TipoDeNormaEnum tipoNorma;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Número", colM = 4, colL = 4, attr = "mask=#####")
	String numeroNorma;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Data", colM = 4, colL = 4)
	LocalDate dataNorma;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "N&ordm;", colM = 2, attr = "mask=###")
	String numeroArtigo;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Artigo", kind = EditKindEnum.TEXTAREA, attr = "rows=2", colM = 10)
	@Column(length = 2048)
	String textoArtigo;

	@Transient
	@Edit(caption = "N&ordm;", colM = 2, attr = { ":validate=act.textoParagrafo ? 'required' : ''", "mask=###" })
	String numeroParagrafo;

	@Transient
	@Edit(caption = "Parágrafo", kind = EditKindEnum.TEXTAREA, attr = { "rows=2",
			":validate=act.numeroParagrafo ? 'required' : ''" }, colM = 10)
	@Column(length = 2048)
	String textoParagrafo;

	@Transient
	@Edit(caption = "N&ordm;", colM = 2, attr = {
			":validate=act.textoInciso || act.textoAlinea || act.numeroAlinea ? 'required' : ''", "mask=AAAAAAA" })
	String numeroInciso;

	@Transient
	@Edit(caption = "Inciso", colM = 10, attr = ":validate=act.numeroInciso || act.textoAlinea || act.numeroAlinea ? 'required' : ''")
	String textoInciso;

	@Transient
	@Edit(caption = "N&ordm;", colM = 2, attr = { ":validate=act.textoAlinea ? 'required' : ''", "mask=aa" })
	String numeroAlinea;

	@Transient
	@Edit(caption = "Alínea", colM = 10, attr = ":validate=act.numeroAlinea ? 'required' : ''")
	String textoAlinea;

	@FullSerialization
	@NonNull
	@ManyToOne
	Norma norma;

	@Override
	public String getDescr() {
		return Utils.buildLink(this.norma, this.norma.getReferencia());
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
	}

	@Override
	public void execute(Agente actor, Agente onBehalfOf, Processo element, EventoProcessual evento, Etiqueta etiqueta)
			throws Exception {
		if (siglaNorma == null)
			throw new RuntimeException("Sigla da norma é obrigatória");
		norma = carregarOuCriar(null, DispositivoDeNormaEnum.NORMA, siglaNorma, nomeNorma);

		if (getNumeroArtigo() == null)
			throw new RuntimeException("Número do artigo é obrigatório");
		norma = carregarOuCriar(norma, DispositivoDeNormaEnum.ARTIGO, numeroArtigo, textoArtigo);

		if (!Utils.empty(getNumeroParagrafo()) && !Utils.empty(getTextoParagrafo()))
			norma = carregarOuCriar(norma, DispositivoDeNormaEnum.PARAGRAFO, numeroParagrafo, textoParagrafo);

		if (!Utils.empty(getNumeroInciso()) && !Utils.empty(getTextoInciso()))
			norma = carregarOuCriar(norma, DispositivoDeNormaEnum.INCISO, numeroInciso, textoInciso);

		if (!Utils.empty(getNumeroAlinea()) && !Utils.empty(getTextoAlinea()))
			norma = carregarOuCriar(norma, DispositivoDeNormaEnum.ALINEA, numeroAlinea, textoAlinea);

		super.execute(actor, onBehalfOf, element, evento, etiqueta);
	}

	private Norma carregarOuCriar(Norma pai, DispositivoDeNormaEnum tipo, String sigla, String texto) {
		HashSet<String> exceto = new HashSet<String>();
		exceto.add("id");

		Norma norma = null;
//		for (Norma i : dao().listarNormaPorIdentificador(pai, tipo, sigla)) {
//			if (texto == null || texto.equals(i.getTexto())) {
//				norma = i;
//				break;
//			}
//		}
		if (norma == null) {
			norma = new Norma(pai, tipo, sigla, texto);
			dao().persist(norma);
		}
		return norma;
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}
}
