package br.jus.trf2.temis.iam.model.enm;

import lombok.AllArgsConstructor;

import com.crivano.jbiz.IEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

@AllArgsConstructor
// @Getter
@Global(singular = "Espécie", plural = "Espécies", gender = Gender.SHE)
public enum AgentePapelEnum implements IEnum {
	PROPRIETARIO("Proprietário", true),
	//
	ADMINISTRADOR("Administrador", true),
	//
	COLABORADOR("Colaborador", false),
	//
	AUDITOR("Auditor", false);

	private final String descr;
	private final boolean administrar;

	@Override
	public Long getId() {
		return (long) this.name().hashCode();
	}

	@Override
	public String getCode() {
		return this.name();
	}

	@Override
	public String getDescr() {
		return this.descr;
	}

}
