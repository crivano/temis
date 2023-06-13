package br.jus.trf2.temis.core;

import java.util.Date;
import java.util.SortedSet;

import com.crivano.jbiz.IEvent;
import com.crivano.jsync.Synchronizable;
import com.crivano.juia.biz.IJuiaEntity;

import br.jus.trf2.temis.core.util.ContextInterceptor;

public interface IEntidade extends IJuiaEntity, Synchronizable {

	Long getId();

	String getSelectFirstLine();

	@Override
	public default void removeChange(IEvent c) {
		IJuiaEntity.super.removeChange(c);
		ContextInterceptor.getDao().remove(c);
	}

	SortedSet<Etiqueta> getEtiqueta();

	void prePersistAndUpdate() throws Exception;

	SortedSet<? extends Evento<?, ?>> getEvento();

	Date getTermino();

	String getDescrCompleta();
}
