package br.jus.trf2.temis.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crivano.swaggerservlet.ISwaggerModel;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.enm.GrupoDeMarcadorEnum;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;

public class Painel {

	public enum TipoDePainelEnum {
		PESSOAL, UNIDADE;
	}

	public static List<MesaItem> listarReferencias(TipoDePainelEnum tipo, Map<Entidade, List<Etiqueta>> references,
			Pessoa pessoa, Unidade unidade, Date currentDate) {
		List<MesaItem> l = new ArrayList<>();

		for (Entidade mobil : references.keySet()) {
			MesaItem r = new MesaItem();
			r.tipo = mobil.getClass().getSimpleName();

			Date datahora = mobil.getModificacao();
			r.datahora = datahora;
			r.tempoRelativo = Utils.calcularTempoRelativo(datahora);

			r.codigo = mobil.getId();
			r.sigla = mobil.getCode();
			r.descr = mobil.getDescr();

			if (mobil.getUnidadeCadastrante() != null)
				r.origem = mobil.getUnidadeCadastrante().getSigla();

			r.grupo = GrupoDeMarcadorEnum.NENHUM.name();
			r.grupoOrdem = Integer.toString(GrupoDeMarcadorEnum.valueOf(r.grupo).ordinal());
			r.grupoNome = GrupoDeMarcadorEnum.valueOf(r.grupo).getNome();

			r.list = new ArrayList<Marca>();

			for (Etiqueta tag : references.get(mobil)) {
				if (tag.getInicio() != null && tag.getInicio().getTime() > currentDate.getTime())
					continue;
				if (tag.getTermino() != null && tag.getTermino().getTime() < currentDate.getTime())
					continue;

				Marca t = new Marca();

				t.nome = tag.getMarcador().getNome();
				t.icone = tag.getMarcador().getIcone();
				t.titulo = Utils.calcularTempoRelativo(tag.getInicio());

				if (tag.getPessoa() != null) {
					t.pessoa = tag.getPessoa().getNome();
				}
				if (tag.getUnidade() != null) {
					t.lotacao = tag.getUnidade().getNome();
				}
				t.inicio = tag.getInicio();
				t.termino = tag.getTermino();

				if (GrupoDeMarcadorEnum.valueOf(r.grupo).ordinal() > tag.getMarcador().getGrupo().ordinal()) {
					r.grupo = tag.getMarcador().getGrupo().name();
					r.grupoOrdem = Integer.toString(tag.getMarcador().getGrupo().ordinal());
					r.grupoNome = tag.getMarcador().getGrupo().getNome();
				}

				r.list.add(t);
				if (pessoa != null && tag.getPessoa() != null) {
					if (pessoa.getId().equals(tag.getPessoa().getId()))
						t.daPessoa = true;
					else
						t.deOutraPessoa = tag.getPessoa() != null;
				}
				if (unidade != null && tag.getUnidade() != null && unidade.getId().equals(tag.getUnidade().getId()))
					t.daLotacao = true;
			}
			l.add(r);
		}

		Collections.sort(l, new Comparator<MesaItem>() {
			@Override
			public int compare(MesaItem o1, MesaItem o2) {
				int i = Integer.compare(Integer.parseInt(o1.grupoOrdem), Integer.parseInt(o2.grupoOrdem));
				if (i != 0)
					return i;
				if (o2.datahora != null) {
					i = o2.datahora.compareTo(o1.datahora);
					if (i != 0)
						return i;
				}
				return 0;
			}
		});
		return l;
	}

	public static class MesaItem implements ISwaggerModel {
		public String tipo;
		public Date datahora;
		public String tempoRelativo;
		public Long codigo;
		public String sigla;
		public String descr;
		public String origem;
		public String grupo;
		public String grupoNome;
		public String grupoOrdem;
		public List<Marca> list;
	}

	public static class Marca implements ISwaggerModel {
		public String pessoa;
		public String lotacao;
		public String nome;
		public String icone;
		public String titulo;
		public Date inicio;
		public Date termino;
		public Boolean daPessoa;
		public Boolean deOutraPessoa;
		public Boolean daLotacao;
	}

}
