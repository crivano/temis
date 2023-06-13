package br.jus.trf2.temis.pjd.model.event;

import javax.persistence.Entity;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Required;

import br.jus.trf2.temis.pjd.model.enm.TipoDeDocumentoEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@Data
@NoArgsConstructor
@Global(singular = "Inclusão de Documento", plural = "Inclusões de Documentos", gender = Gender.SHE, action = "Incluir Documento", icon = "fas fa-paperclip")
public class EventoProcessualInclusaoDeDocumentoGenerico extends EventoProcessualInclusaoDeDocumento {
	@Required
	@NonNull
	@Edit(caption = "Tipo de Documento", colM = 12)
	TipoDeDocumentoEnum tipoDeDocumento;

}
