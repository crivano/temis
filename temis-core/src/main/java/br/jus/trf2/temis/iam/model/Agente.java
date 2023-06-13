package br.jus.trf2.temis.iam.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.crivano.jbiz.IActor;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.Entidade;

import com.crivano.juia.annotations.Search;

import lombok.Data;

@Entity
@Data
@Global(singular = "Agente", plural = "Agentes", gender = Gender.HE, locator = "iam-agente")
public abstract class Agente extends Entidade implements IActor {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Edit
	@Search
	String identidade;
	@Edit
	String senha;
}
