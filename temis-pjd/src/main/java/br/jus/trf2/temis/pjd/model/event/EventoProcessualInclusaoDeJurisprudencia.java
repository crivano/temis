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
import br.jus.trf2.temis.core.util.ContextInterceptor;
import br.jus.trf2.temis.core.util.Dao;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.core.util.ModeloUtils;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.Agente;
import br.jus.trf2.temis.pjd.model.Jurisprudencia;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.Processo.EventoProcessual;
import br.jus.trf2.temis.pjd.model.enm.TipoDeJurisprudencia;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Jurisprudência", plural = "Inclusões de Jurisprudências", gender = Gender.SHE, action = "Incluir Jurisprudência", icon = "fas fa-plus")
@FieldNameConstants
public class EventoProcessualInclusaoDeJurisprudencia extends Processo.EventoProcessual {

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Tipo de Jurisprudência", colM = 12)
	TipoDeJurisprudencia tipoJurisprudencia;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Sigla do Tribunal", colM = 3)
	String siglaTribunal;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Nome do Órgao Julgador", colM = 9)
	String nomeOrgaoJulgador;

	@Transient
	@Edit(caption = "N&ordm; do Processo", colM = 6, attr = { "mask=#######-##.####.#.##.####",
			":validate=act." + Fields.numeroAntigoDoProcesso + " ? '' : 'required'" })
	String numeroProcesso;

	@Transient
	@Edit(caption = "N&ordm; Antigo do Processo", colM = 6)
	String numeroAntigoDoProcesso;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Data do Julgamento", colM = 3, attr = "validate=required")
	LocalDate dataJulgamento;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Data da Publicação", colM = 3, attr = "validate=required")
	LocalDate dataPublicacao;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Nome do Relator", colM = 6)
	String nomeRelator;

	@Transient
	@Required
	@NonNull
	@Edit(caption = "Ementa", kind = EditKindEnum.TEXTAREA, attr = "rows=3", colM = 6)
	@Column(length = 2048)
	String textoEmenta;

	@Transient
	@Required
	@NonNull
	@Column(length = 2048)
	@Edit(caption = "Certidão de Julgamento", kind = EditKindEnum.TEXTAREA, attr = "rows=3", colM = 6)
	String textoCertidaoDeJulgamento;

	@FullSerialization
	@NonNull
	@ManyToOne
	Jurisprudencia jurisprudencia;

	@Override
	public String getDescr() {
		return Utils.buildLink(this.jurisprudencia, null) + ": " + this.jurisprudencia.getDescr();
	}

	@Override
	protected void addMiniActions(SortedSet<Acao> set) {
		set.add(new ExcluirMiniAction());
	}

	@Override
	public void execute(Agente actor, Agente onBehalfOf, Processo element, EventoProcessual evento, Etiqueta etiqueta)
			throws Exception {
		Dao dao = ContextInterceptor.getDao();
		HashSet<String> exceto = new HashSet<String>();
		exceto.add("id");
		Jurisprudencia jurisprudencia = null;

		if (numeroProcesso != null) {
//			for (Jurisprudencia i : dao.listarJurisprudenciaPorNumeroDeProcesso(this.numeroProcesso)) {
//				if (ModeloUtils.compareProperties(i, this, exceto)) {
//					jurisprudencia = i;
//					break;
//				}
//			}
		}

		if (jurisprudencia == null) {
			jurisprudencia = new Jurisprudencia();
			ModeloUtils.copyProperties(jurisprudencia, this, exceto);
			dao.persist(jurisprudencia);
		}

		this.jurisprudencia = jurisprudencia;
		super.execute(actor, onBehalfOf, element, evento, etiqueta);
	}

	@Override
	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SE_AUDITANDO;
	}

}
