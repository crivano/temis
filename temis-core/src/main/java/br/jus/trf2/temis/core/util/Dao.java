package br.jus.trf2.temis.core.util;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.beanutils.PropertyUtils;

import com.crivano.jsync.Operation;
import com.crivano.jsync.OperatorWithHistory;
import com.crivano.jsync.Synchronizer;
import com.crivano.juia.annotations.Global;
import com.crivano.swaggerservlet.SwaggerUtils;

import br.jus.trf2.temis.core.Arquivo;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.IEntidade;
import br.jus.trf2.temis.iam.model.Endereco;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;
import lombok.NonNull;

@RequestScoped
public class Dao {
	private EntityManager em;

	@Inject
	private void setEm(EntityManager em) {
		this.em = em;
	}

	public List listarDocumentosPorPessoaOuLotacao(Pessoa pessoa, Unidade unidade) {

		long tempoIni = System.nanoTime();
		Query query = em.createQuery("select e, t from Entidade e" + " inner join e.etiqueta t"
				+ " where (t.inicio is null or t.inicio < CURRENT_TIMESTAMP)"
				+ " and (t.termino is null or t.termino > CURRENT_TIMESTAMP)" + " and (t.pessoa.id = :pessoa or "
				+ " t.pessoa.id is null) and (t.unidade.id = :unidade or t.unidade is null)");

		if (pessoa != null)
			query.setParameter("pessoa", pessoa.getId());
		else
			query.setParameter("pessoa", null);
		if (unidade != null)
			query.setParameter("unidade", unidade.getId());
		else
			query.setParameter("unidade", null);

		List l = query.getResultList();
		long tempoTotal = System.nanoTime() - tempoIni;
		// System.out.println("consultarPorFiltroOtimizado: " + tempoTotal
		// / 1000000 + " ms -> " + query + ", resultado: " + l);
		return l;
	}

	public Date consultarDataEHoraDoServidor() {
		return new Date();
	}

	public void remove(Object entity) {
		em.remove(entity);
	}

	protected boolean isVersionable(Class<?> clazz) {
		Global global = clazz.getAnnotation(Global.class);
		if (global == null)
			return false;
		return global.versionable();
	}

	protected boolean isEntity(Class<?> clazz) {
		return Entidade.class.isAssignableFrom(clazz);
	}

	public void persist(Object entity) {
		em.persist(entity);
	}

	public void persistEntidade(IEntidade data) {
		try {
			Synchronizer sync = new Synchronizer();

			Long id = (Long) getIdentifier(data);
			Date dt = consultarDataEHoraDoServidor();
			List<Field> fields = ModeloUtils.getFieldList(data.getClass());

			// Se a entidade já existe
			if (id != null) {
				// Acrescenta a versão antiga na lista para sincronismo
				IEntidade oldData = em.find(data.getClass(), id);
				sync.addOld(oldData);

				// Varre todos os campos e localiza coleções de entidades
				for (Field fld : fields) {
					OneToMany o2m = fld.getAnnotation(OneToMany.class);

					// lista marcada com @OneToMany
					if (o2m != null && Utils.sorn(o2m.mappedBy()) != null) {
						Collection<IEntidade> l = (Collection<IEntidade>) fld.get(oldData);
						if (l != null) {
							int i = 0;
							for (IEntidade oOld : l) {
								if (oOld.getTermino() != null)
									continue;
								// Acrescenta as entidades antigas na lista velha para sincronismo.
								// Isso não parece fazer muito sentido, pois se a entidade é nova, imagino que
								// as listas de entidades subordinadas não devam vir preenchidas com entidades
								// existentes no banco.
								sync.addOld(oOld);
							}
						}
					}
				}
			}

			// Acrescenta a versão antiga na lista para sincronismo
			sync.addNew(data);

			// Varre todos os campos
			for (Field fld : fields) {
				ManyToOne m2o = fld.getAnnotation(ManyToOne.class);
				OneToMany o2m = fld.getAnnotation(OneToMany.class);

				// Campo marcado com @ManyToOne
				if (m2o != null) {
					IEntidade e = (IEntidade) fld.get(data);
					System.out.println(fld.getName());
					if (e != null) {
						if (e.getId() != null) {
							// Carrega a entidade referenciada do banco, eliminando qualquer possível
							// alteração introduzida pela interface gráfica
							fld.set(data, em.find(e.getClass(), e.getId()));
						}
					}
				}

				// lista marcada com @OneToMany
				if (o2m != null && Utils.sorn(o2m.mappedBy()) != null) {
					Collection<Entidade> l = (Collection<Entidade>) fld.get(data);
					if (l != null) {
						int i = 0;
						for (Entidade oOrig : l) {
							// Atribui o campo de ordenação, começando por 1
							i++;
							Global juiaGlobal = oOrig.getClass().getAnnotation(Global.class);
							if (juiaGlobal != null && juiaGlobal.sortField().length() > 0)
								PropertyUtils.setProperty(oOrig, juiaGlobal.sortField(), i);
							// Faz o link reverso preenchendo o campo indicado por mappedBy()
							for (Field fldObj : ModeloUtils.getFieldList(oOrig.getClass())) {
								if (o2m.mappedBy().equals(fldObj.getName())) {
									fldObj.set(oOrig, data);
								}
							}
							if (oOrig.getTermino() != null)
								continue;
							sync.addNew(oOrig);
						}
					}
				}
			}
			sync.sync(new OperatorWithHistory<Entidade>() {
				@Override
				public void init(Operation<Entidade> opr) {
					Entidade novo = opr.getNew();
					Entidade antigo = opr.getOld();
					SwaggerUtils.log(Dao.class).info("inicializando: " + opr);
					if (novo != null) {
						novo.setId(null);
						novo.setInicio(dt);
					}
					if (antigo != null)
						antigo.setTermino(dt);
				}

				@Override
				public Entidade insert(Entidade novo) {
					SwaggerUtils.log(Dao.class).info("acrescentando: " + novo.toString());
					em.persist(novo);
					novo.setIdInicial(novo.getId());
					return novo;
				}

				@Override
				public Entidade remove(Entidade antigo) {
					SwaggerUtils.log(Dao.class).info("removendo: " + antigo.toString());
					// em.remove(antigo);
					return antigo;
				}

				@Override
				public Entidade update(Entidade antigo, Entidade novo) {
					SwaggerUtils.log(Dao.class).info("alterando: " + novo.toString());
					novo.setIdInicial(antigo.getIdInicial());
					em.persist(novo);
					return novo;
				}
			});
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void persistEntidadeOld(Entidade data) {
		Long id = (Long) getIdentifier(data);
		Date dt = consultarDataEHoraDoServidor();

		if (id != null) {
			Entidade oldData = em.find(data.getClass(), id);
			boolean f = false;
			if (isVersionable(data.getClass())) {
				Set<String> exceto = new HashSet<>();
				exceto.add(Entidade.Fields.id);
				exceto.add(Entidade.Fields.idInicial);
				exceto.add(Entidade.Fields.inicio);
				exceto.add(Entidade.Fields.termino);
				exceto.add(Entidade.Fields.pessoaCadastrante);
				exceto.add(Entidade.Fields.unidadeCadastrante);
				f = ModeloUtils.compareProperties(oldData, data, exceto);
			}
			if (!f) {
				persist(oldData, data, dt);
				ajustarListas(data, dt);
			} else {
				ModeloUtils.copyProperties(oldData, data, null);
				data = oldData;
			}
		} else {
			ModeloUtils.initProperties(data);
		}
		try {
			data.prePersistAndUpdate();
			em.persist(data);
			if (isVersionable(data.getClass()) && data.getIdInicial() == null) {
				data.setIdInicial(data.getId());
				em.persist(data);
			}
		} catch (Exception ex) {
			SwaggerUtils.log(this.getClass()).error("NPE", ex);
		}

	}

	private void ajustarListas(Entidade data, Date dt) {
		try {
			for (Field fld : ModeloUtils.getFieldList(data.getClass())) {
				OneToMany o2m = fld.getAnnotation(OneToMany.class);
				if (o2m == null || Utils.sorn(o2m.mappedBy()) == null)
					continue;

				// lista marcada com @OneToMany
				Collection<Entidade> l = (Collection<Entidade>) fld.get(data);
				if (l != null) {
					int i = 0;
					for (Entidade oOrig : l) {
						Long id = (Long) getIdentifier(data);
						if (id != null) {
							Entidade oldData = em.find(oOrig.getClass(), id);
							persist(oldData, data, dt);
						}
						data.setInicio(dt);
						// Remove a ID da entidade na lista para forçar a regravação
						if (oOrig instanceof Entidade)
							((Entidade) oOrig).setId(null);
						// Atribui o campo de ordenação, começando por 1
						i++;
						Global juiaGlobal = oOrig.getClass().getAnnotation(Global.class);
						if (juiaGlobal != null && juiaGlobal.sortField().length() > 0)
							PropertyUtils.setProperty(oOrig, juiaGlobal.sortField(), i);
						// Faz o link reverso preenchendo o campo indicado por mappedBy()
						for (Field fldObj : ModeloUtils.getFieldList(oOrig.getClass())) {
							if (o2m.mappedBy().equals(fldObj.getName())) {
								fldObj.set(oOrig, data);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void persist(Entidade oldData, Entidade newData, Date dt) {
		oldData.setTermino(dt);
		em.persist(oldData);
		newData.setInicio(dt);
		newData.setIdInicial(oldData.getIdInicial());
		newData.setId(null);
	}

	public void flush() {
		em.flush();
	}

	public Long getIdentifier(Object entity) {
		return (Long) em.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
	}

	public List<Pessoa> listarPessoaPorCpfCnpj(@NonNull String cpfcnpj) {
		TypedQuery<Pessoa> q = em.createQuery("from Pessoa where " + field(Pessoa.Fields.cpfcnpj, "="), Pessoa.class);
		q.setParameter(Pessoa.Fields.cpfcnpj, cpfcnpj);
		return q.getResultList();
	}

	public List<Endereco> listarEndercoPorIdPessoa(@NonNull Pessoa pessoa) {
		TypedQuery<Endereco> q = em.createQuery("from Endereco e where " + field(Endereco.Fields.pessoa, "="),
				Endereco.class);
		q.setParameter(Endereco.Fields.pessoa, pessoa);
		return q.getResultList();
	}

	private String field(String fieldName, String operand) {
		return fieldName + " " + operand + " :" + fieldName;
	}

	public Arquivo find(Class<Arquivo> clazz, Long key) {
		return em.find(clazz, key);
	}

//	public List<Jurisprudencia> listarJurisprudenciaPorNumeroDeProcesso(@NonNull String numeroProcesso) {
//		TypedQuery<Jurisprudencia> q = em.createQuery(
//				"from Jurisprudencia where " + field(Jurisprudencia.Fields.numeroProcesso, "="), Jurisprudencia.class);
//		q.setParameter(Jurisprudencia.Fields.numeroProcesso, numeroProcesso);
//		return q.getResultList();
//	}

//	public List<Norma> listarNormaPorIdentificador(Norma normaPai, DispositivoDeNormaEnum dispositivo, String sigla) {
//		TypedQuery<Norma> q = em.createQuery(
//				"from Norma where " + (normaPai != null ? field(Norma.Fields.normaPai, "=") + " and " : "")
//						+ field(Norma.Fields.dispositivo, "=") + " and " + field(Norma.Fields.identificador, "="),
//				Norma.class);
//		if (normaPai != null)
//			q.setParameter(Norma.Fields.normaPai, normaPai);
//		q.setParameter(Norma.Fields.dispositivo, dispositivo);
//		q.setParameter(Norma.Fields.identificador, Norma.buildFragmentoDeIdentificador(sigla));
//		return q.getResultList();
//	}

}
