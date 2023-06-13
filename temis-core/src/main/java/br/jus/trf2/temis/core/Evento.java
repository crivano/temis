package br.jus.trf2.temis.core;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.auth0.jwt.internal.org.apache.commons.lang3.ObjectUtils;
import com.crivano.jbiz.IActor;
import com.crivano.jbiz.IEvent;
import com.crivano.jlogic.Expression;
import com.crivano.jsync.IgnoreForSimilarity;
import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.biz.IJuiaEvent;

import br.jus.trf2.temis.core.logic.PodeSim;
import br.jus.trf2.temis.core.util.ContextInterceptor;
import br.jus.trf2.temis.core.util.Dao;
import br.jus.trf2.temis.core.util.ModeloUtils;
import br.jus.trf2.temis.iam.model.Agente;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@MappedSuperclass
@Data
@FieldNameConstants
public abstract class Evento<E extends IEntidade, V extends Evento<E, V>> implements
		IJuiaEvent<E, Agente, V, Acao<E, Agente, V>, Etiqueta>, Acao<E, Agente, V>, Comparable<Evento<E, ?>> {
	@IgnoreForSimilarity
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Edit
	private Long id;

	@ManyToOne
	private Agente agente;

	private Date dtIni;
	private Date dtFim;
	private String descr;

	@ManyToOne()
	private V cancelador;
	@ManyToOne
	private V desativador;
	@ManyToOne
	private V referente;

	public SortedSet<Acao<E, ?, V>> getMiniActions() {
		SortedSet<Acao<E, ?, V>> actions = new TreeSet<>(new Comparator<Acao<E, ?, V>>() {
			@Override
			public int compare(Acao<E, ?, V> o1, Acao<E, ?, V> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		addMiniActions((SortedSet<Acao>) (Object) actions);
		return actions;
	}

	protected void addMiniActions(SortedSet<Acao> set) {
	}

	public boolean isAtiva() {
		return cancelador == null && desativador == null;
	}

	public String getTipo() {
		return this.getClass().getAnnotation(Global.class).singular();
	}

	public String getTitulo() {
		return null;
	}

	@Override
	public void execute(Agente actor, Agente onBehalfOf, E entity, V event, Etiqueta tag) throws Exception {
		entity.addEvent(this);
		String mappedBy = entity.getClass().getDeclaredField("evento").getAnnotation(OneToMany.class).mappedBy();
		Class clazz = this.getClass();
		for (Field f : ModeloUtils.getFieldList(this.getClass())) {
			if (f.getName().equals(mappedBy)) {
				f.setAccessible(true);
				f.set(this, entity);
			}
		}
		if (event != null)
			this.setReferente(event);
		if (entity.getId() == null)
			ContextInterceptor.getDao().persist(entity);
		ContextInterceptor.getDao().flush();
	}

	@Override
	public Expression getActiveMiniAction(Agente actor, Agente onBehalfOf, E element, Acao miniAction) {
		return new PodeSim();
	}

	public Expression getRequiredMiniAction(Agente actor, Agente onBehalfOf, E element, Acao miniAction) {
		return null;
	}

	@Override
	public void init(E element) {
	}

	@Override
	public void beforeValidate(E element) {
	}

	@Override
	public Date getBegin() {
		return dtIni;
	}

	@Override
	public IActor getActor() {
		return agente;
	}

	@Override
	public Date getFinish() {
		return dtFim;
	}

	@Override
	public IEvent getCanceledBy() {
		return cancelador;
	}

	@Override
	public int compareTo(Evento<E, ?> that) {
		int i = ObjectUtils.compare(getBegin(), that.getBegin());
		if (i != 0)
			return i;
		i = ObjectUtils.compare(getId(), that.getId());
		if (i != 0)
			return i;
		return 0;
	}

	@PrePersist
	@PreUpdate
	public void prePersistAndUpdate() throws Exception {
		if (getBegin() == null)
			setDtIni(new Date());
	}

	public Dao dao() {
		return ContextInterceptor.getDao();
	}

	public VisibilidadeDeEventoEnum getVisibilidade() {
		return VisibilidadeDeEventoEnum.EXIBIR_SEMPRE;
	}

	public Long getEntiId() {
		return id;
	}
}
