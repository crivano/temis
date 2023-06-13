package br.jus.trf2.temis.model;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import br.jus.trf2.temis.core.enm.MarcadorEnum;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Tag implements Comparable<Tag> {
	private Pessoa pessoa;
	private Unidade unidade;
	@Enumerated(EnumType.STRING)
	private MarcadorEnum marcador;
	private Date begin;
	private Date finish;

	public Tag() {
		super();
	}

	@Override
	public int compareTo(Tag o) {
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

		if (begin != null) {
			i = begin.compareTo(o.begin);
			if (i != 0)
				return i;
		} else if (o.begin != null)
			return 1;
		if (finish != null) {
			i = finish.compareTo(o.finish);
			if (i != 0)
				return i;
		} else if (o.finish != null)
			return 1;
		return 0;
	}

}
