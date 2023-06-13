package br.jus.trf2.temis.pjd.model.event;

import javax.persistence.Entity;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.pjd.model.enm.TipoDeDocumentoComprobatorioDeEppEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Documento Comprobatório de EPP", plural = "Inclusões de Documentos Comprobatórios de EPP", action = "Incluir Documento Comprobatório de EPP", gender = Gender.SHE, icon = "fas fa-paperclip")
public class EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp extends EventoProcessualInclusaoDeDocumento {
	@Required
	@NonNull
	@Edit(caption = "Tipo de Documento", colM = 12)
	TipoDeDocumentoComprobatorioDeEppEnum tipoDeDocumento = TipoDeDocumentoComprobatorioDeEppEnum.CARTAO_DO_CNPJ;
}
