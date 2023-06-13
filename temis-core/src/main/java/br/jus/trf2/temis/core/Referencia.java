package br.jus.trf2.temis.core;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Id;

public class Referencia {
	@Id
	private long id;

	private String classe;

	private SortedSet<String> index = new TreeSet<>();

	private String kindElement;

	private String code;

	private String descr;

	private Date modify;

//		private Set<Ref<IXrpElement>> referenced = new HashSet<>();

}
