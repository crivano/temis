package br.jus.trf2.temis.core;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.crivano.jbiz.ITag;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;

import br.jus.trf2.temis.core.enm.MarcadorEnum;
import br.jus.trf2.temis.core.util.ModeloUtils.Desconsiderar;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@FieldNameConstants
@Global(singular = "Etiqueta", plural = "Etiquetas", gender = Gender.SHE)
public class Etiqueta implements ITag, Comparable<Etiqueta> {
	@Desconsiderar
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@NonNull
	@ManyToOne
	private Entidade entidade;

	private Long idEvento;

	@ManyToOne
	private Pessoa pessoa;

	@ManyToOne
	private Unidade unidade;

	@NotNull
	@NonNull
	private MarcadorEnum marcador;

	@NotNull
	@NonNull
	private Date inicio;

	private Date termino;

	@Override
	public int compareTo(Etiqueta o) {
		int i = marcador.compareTo(o.marcador);
		if (i != 0)
			return i;

		if (unidade != null) {
			i = unidade.compareTo(o.unidade);
			if (i != 0)
				return i;
		} else if (o.unidade != null)
			return 1;

		if (pessoa != null) {
			i = pessoa.compareTo(o.pessoa);
			if (i != 0)
				return i;
		} else if (o.pessoa != null)
			return 1;

		if (inicio != null) {
			i = inicio.compareTo(o.inicio);
			if (i != 0)
				return i;
		} else if (o.inicio != null)
			return 1;
		if (termino != null) {
			i = termino.compareTo(o.termino);
			if (i != 0)
				return i;
		} else if (o.termino != null)
			return 1;
		return 0;
	}

}
