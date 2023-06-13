package br.jus.trf2.temis.core;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.crivano.jbiz.IEvent;
import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.util.JuiaUtils;

import br.jus.trf2.temis.core.util.ContextInterceptor;
import br.jus.trf2.temis.core.util.ModeloUtils;
import br.jus.trf2.temis.core.util.NoSerialization;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 50)
@Data
@FieldNameConstants
public abstract class Entidade implements IEntidade {

	@IgnoreForSimilarity
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Edit
	private Long id;

	// private Long first;

	@IgnoreForSimilarity
	private Date inicio;
	@IgnoreForSimilarity
	private Date termino;
	@IgnoreForSimilarity
	private Date modificacao;
	@IgnoreForSimilarity
	private Long idInicial;

	@ManyToOne
	private Pessoa pessoaCadastrante;
	@ManyToOne
	private Unidade unidadeCadastrante;

	@NoSerialization
	@OneToMany(mappedBy = Etiqueta.Fields.entidade, cascade = CascadeType.ALL)
	@OrderBy(Etiqueta.Fields.inicio)
	private SortedSet<Etiqueta> etiqueta = new TreeSet<>();

	// Último Acesso
	private Date access;

	// Última alteração
//	private Date modify;
//
//	private boolean active = true;
//
//	private Set<String> signaling;

	@Override
	public String getCode() {
		if (getId() == null)
			return null;
		return getId().toString();
	}

	@Override
	public String getDescr() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescrCompleta() {
		return null;
	}

	@Override
	public String getSelectFirstLine() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSelectSecondLine() {
		// TODO Auto-generated method stub
		return null;
	}

	protected static String concat(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			if (s == null || "".equals(s))
				continue;
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(s);
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}

	protected void fixMappedBy() {
		for (Field fld : ModeloUtils.getFieldList(this.getClass())) {
			OneToMany o2m = fld.getAnnotation(OneToMany.class);
			if (o2m == null || JuiaUtils.sorn(o2m.mappedBy()) == null)
				continue;
			Collection<?> l;
			try {
				l = (Collection<?>) fld.get(this);
				if (l == null)
					continue;
				for (Object obj : l) {
					for (Field fldObj : ModeloUtils.getFieldList(obj.getClass())) {
						if (o2m.mappedBy().equals(fldObj.getName())) {
							fldObj.set(obj, this);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public SortedSet<? extends IEvent<?>> getEvent() {
		return (SortedSet<? extends IEvent<?>>) getEvento();
	}

	public SortedSet<? extends Evento<?, ?>> getEvento() {
		return null;
	}

	@Override
	public Date getBegin() {
		return getInicio();
	}

	@Override
	public void setBegin(Date dt) {
		setInicio(dt);
	}

	public Date getFinish() {
		return getTermino();
	}

	public void setFinish(Date dt) {
		setTermino(dt);
	}

	@Override
	public void removeChange(IEvent c) {
		IEntidade.super.removeChange(c);
		ContextInterceptor.getDao().remove(c);
	}

	public void prePersistAndUpdate() throws Exception {
		if (getBegin() == null)
			setBegin(new Date());

		SortedSet<Etiqueta> lOrig = (SortedSet<Etiqueta>) (SortedSet) getTags();
		SortedSet<Etiqueta> lDest = getEtiqueta();

		SortedSet<Etiqueta> lTemp = new TreeSet<>();
		lTemp.addAll(lDest);
//		lDest.clear();
		if (lOrig != null) {
			for (Etiqueta oDest : lDest) {
				boolean encontrado = false;
				// remover itens de destino que não existem na origem
				for (Etiqueta oOrig : lOrig) {
					if (ModeloUtils.semelhante(oOrig, oDest)) {
						encontrado = true;
						break;
					}
				}
				if (!encontrado) {
					lDest.remove(oDest);
					ContextInterceptor.getDao().remove(oDest);
				}
			}
			for (Etiqueta oOrig : lOrig) {
				boolean encontrado = false;
				// remover itens de destino que não existem na origem,
				// atualizar itens que existem nos dois e
				for (Etiqueta oDest : lTemp) {
					if (ModeloUtils.semelhante(oOrig, oDest)) {
						lDest.add(oDest);
						encontrado = true;
						break;
					}
				}
				if (!encontrado) {
					// inserir itens que só existem na origem
					lDest.add(oOrig);
				}
			}
		}
	}

	public Long getEntiId() {
		return id;
	}
}
