package br.jus.trf2.temis.core.controller;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.crivano.jbiz.IEntity;
import com.crivano.jbiz.IEnum;
import com.crivano.jlogic.Expression;
import com.crivano.juia.AnnotationViewBuilder;
import com.crivano.juia.View.Kind;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.biz.IJuiaAction;
import com.crivano.juia.html.HtmlTemplateBuilder;
import com.crivano.juia.util.JuiaUtils;
import com.webfirmframework.wffweb.tag.html.AbstractHtml;
import com.webfirmframework.wffweb.tag.html.attribute.global.ClassAttribute;
import com.webfirmframework.wffweb.tag.html.attribute.global.Id;
import com.webfirmframework.wffweb.tag.html.attribute.global.Style;
import com.webfirmframework.wffweb.tag.html.attribute.global.Title;
import com.webfirmframework.wffweb.tag.html.attributewff.CustomAttribute;
import com.webfirmframework.wffweb.tag.html.formatting.I;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Button;
import com.webfirmframework.wffweb.tag.html.formsandinputs.Form;
import com.webfirmframework.wffweb.tag.html.links.A;
import com.webfirmframework.wffweb.tag.html.lists.Li;
import com.webfirmframework.wffweb.tag.html.lists.Ul;
import com.webfirmframework.wffweb.tag.html.stylesandsemantics.Div;
import com.webfirmframework.wffweb.tag.html.tables.TBody;
import com.webfirmframework.wffweb.tag.html.tables.THead;
import com.webfirmframework.wffweb.tag.html.tables.Table;
import com.webfirmframework.wffweb.tag.html.tables.Td;
import com.webfirmframework.wffweb.tag.html.tables.Th;
import com.webfirmframework.wffweb.tag.html.tables.Tr;
import com.webfirmframework.wffweb.tag.htmlwff.CustomTag;
import com.webfirmframework.wffweb.tag.htmlwff.NoTag;

import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.http.iogi.InstantiatorWithErrors;
import br.com.caelum.vraptor.proxy.CDIProxies;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.ValidationException;
import br.com.caelum.vraptor.validator.Validator;
import br.com.caelum.vraptor.view.Results;
import br.jus.trf2.temis.core.Acao;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.IEntidade;
import br.jus.trf2.temis.core.VisibilidadeDeEventoEnum;
import br.jus.trf2.temis.core.util.AppResources;
import br.jus.trf2.temis.core.util.BadRequestException;
import br.jus.trf2.temis.core.util.Dao;
import br.jus.trf2.temis.core.util.DataUtils;
import br.jus.trf2.temis.core.util.ModeloUtils;
import br.jus.trf2.temis.core.util.Texto;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.Usuario;

public abstract class ControllerSupport<T extends IEntidade> {
	Logger logger = Logger.getLogger(this.getClass().getCanonicalName());

	@Inject
	protected Result result;
	@Inject
	protected HttpServletRequest request;
	@Inject
	protected HttpServletResponse response;
	@Inject
	protected EntityManagerFactory emf;
	@Inject
	protected EntityManager em;
	@Inject
	protected AppResources resources;
	@Inject
	protected Dao dao;

	protected boolean fAdmin;
	protected String userEmail;

	protected Class<T> myClass = getMyClass();

	private InstantiatorWithErrors instantiator;

	private Validator validator;

//	@Inject
//	protected void inject(Result result, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		this.result = result;
//		this.request = request;
//		this.response = response;
//
////		if (userService.isUserLoggedIn()) {
////			this.userEmail = userService.getCurrentUser().getEmail().toLowerCase();
////			result.include("logout", userService.createLogoutURL("/"));
////		}
////		this.fAdmin = userService.isUserLoggedIn() && userService.isUserAdmin();
//	}
//
	private class Cfg {
		String singular;
	};

	@Get
	@Path("config")
	public void config() {
		Cfg cfg = new Cfg();
		Global juiaGlobal = (Global) myClass.getAnnotation(Global.class);
		if (juiaGlobal != null) {
			cfg.singular = juiaGlobal.singular();
		}
		result.use(Results.json()).withoutRoot().from(cfg).recursive().serialize();
	}

	@Get
	@Path("dados/{key}")
	public void get(Long key) throws Exception {
//		assertUsuarioAutorizado(key);
		T v = em.find(myClass, key);
		ModeloUtils.loadProperties(v);
		result.use(Results.json()).withoutRoot().from(v).recursive().serialize();
	}

	@Get
	@Path("novo")
	public void getNew(String keyOriginal) throws Exception {
//		if (keyOriginal == null) {
//			Usuario usuario = ContextInterceptor.assertUsuarioCorrente();
//			T v = myClass.newInstance();
//			result.use(Results.json()).withoutRoot().from(v).recursive().serialize();
//		} else {
//			Usuario usuario = assertUsuarioAutorizado(keyOriginal);
//
//			// Get data
//			//
//			T data = dao.load(keyOriginal, myClass);
//			if (!(data instanceof XrpElementSupport))
//				throw new Exception("Só é possível duplicar classes descendentes de XrpElementSupport");
//			XrpElementSupport e = (XrpElementSupport) data;
//			e.setId(null);
//			e.setChange(new TreeSet<IChange>());
//			e.onDuplicate();
//			result.use(Results.json()).withoutRoot().from(e).recursive().serialize();
//		}
	}

	private static class DtoAcao implements Comparable<DtoAcao> {
		String name;
		String slug;
		String icon;
		String click;
		boolean active;
		String explanation;
		boolean required;
		String requiredExplanation;

		@Override
		public int compareTo(DtoAcao o) {
			if (this.required && !o.required)
				return -1;
			if (!this.required && o.required)
				return 1;
			if (this.active && !o.active)
				return -1;
			if (!this.active && o.active)
				return 1;
			int i = this.name.compareTo(o.name);
			if (i != 0)
				return i;
			return 0;
		}
	}

	private static class DtoChange {
		Long id;
		Date dt;
		String tempoRelativo;
		String tipo;
		String titulo;
		String slug;
		String descr;
		String agente;
		VisibilidadeDeEventoEnum visibilidade;

		SortedSet<DtoAcao> action;
		public Long idDesativador;
		public Long idCancelador;
	}

	private static class DtoDadosEAcoes {
		IEntity data;
		SortedSet<DtoAcao> action;
		List<DtoChange> event;
//		ControleDeAcesso cda;
		List<DtoTag> tag = new ArrayList<>();
//		List<XrpInboxReference> mentionedBy;
	}

	@Get
	@Path("dadoseacoes/{key}")
	public void getDataAndActions(Long key) throws Exception {
		Usuario usuario = assertUsuarioAutorizado(key);

		// Get data
		//
		T data = em.find(myClass, key);
		ModeloUtils.loadProperties(data);

		DtoDadosEAcoes dea = new DtoDadosEAcoes();
		dea.data = data;

//		data.onView();

		// Get tags, CDA and mentions
//		XrpReference reference = dao.load(Key.create(keyEmpresa, XrpReference.class, key));
//		List<XrpReference> listRef = DaoUtils.getElementRefs((Key<IXrpElement>) (Key) Key.create(key));
//		dea.cda = dao.load(Key.create(Key.create(data), ControleDeAcesso.class, ControleDeAcesso.ID_FIXA));

		// Get changes
		//
		if (data.getEvent() != null) {
			List<DtoChange> l = new ArrayList<>();
			for (Evento evento : (SortedSet<Evento>) (SortedSet) data.getEvent()) {
				DtoChange a = new DtoChange();
				a.id = evento.getId();
				if (evento.getDesativador() != null)
					a.idDesativador = evento.getDesativador().getId();
				if (evento.getCancelador() != null)
					a.idCancelador = evento.getCancelador().getId();
				a.dt = evento.getDtIni();
				a.tempoRelativo = DataUtils.calcularTempoRelativo(evento.getDtIni());
				a.titulo = evento.getTitulo();
				a.slug = Texto.slugify(a.titulo, true, false);
				a.visibilidade = evento.getVisibilidade();
				a.tipo = evento.getTipo();
				if (evento.getAgente() != null)
					a.agente = evento.getAgente().getDescr();
				a.descr = evento.getDescr();
				SortedSet<Acao> miniactions = evento.getMiniActions();
				if (miniactions != null && evento.isAtiva()) {
					a.action = new TreeSet<>();
					for (Acao ma : miniactions) {
						DtoAcao dma = new DtoAcao();
						dma.name = ma.getName();
						dma.slug = Texto.slugify(dma.name, true, false);
						dma.icon = ma.getIcon();
						dma.click = ma.getClick(data, evento);
						Expression exp = evento.getActiveMiniAction(usuario, usuario, data, ma);
						dma.active = exp == null ? true : exp.eval();
						dma.explanation = "Está " + (dma.active ? "habilitado" : "desabilitado") + " porque "
								+ (exp == null ? "sempre pode" : Utils.formatExplanation(exp.explain(dma.active)));
						Expression requiredExp = evento.getRequiredMiniAction(usuario, usuario, data, ma);
						dma.required = requiredExp == null ? false : requiredExp.eval();
						dma.requiredExplanation = (dma.required ? "Está" : "Não está") + " pendente porque "
								+ (requiredExp == null ? "nunca pode"
										: Utils.formatExplanation(requiredExp.explain(dma.required)));
						a.action.add(dma);
					}

				}
				l.add(a);
			}
			dea.event = l;
		}

		// Get actions
		//
		SortedSet<IJuiaAction> xrpActions = data.getActions();
		if (xrpActions != null) {
			SortedSet<DtoAcao> l = new TreeSet<>();
			for (IJuiaAction xrpa : xrpActions) {
				DtoAcao a = new DtoAcao();
				a.name = xrpa.getName();
				a.slug = Texto.slugify(a.name, true, false);
				a.icon = xrpa.getIcon();
				a.click = xrpa.getClick(data, null);
				Expression exp = xrpa.getActive(data, null);
				a.active = exp == null ? true : exp.eval();
				a.explanation = exp == null ? "sempre pode"
						: "Está " + (a.active ? "habilitado" : "desabilitado") + " porque "
								+ Utils.formatExplanation(exp.explain(a.active));
				Expression requiredExp = xrpa.getRequired(data, null);
				a.required = requiredExp == null ? false : requiredExp.eval();
				a.requiredExplanation = (a.required ? "Está" : "Não está") + " pendente porque "
						+ (requiredExp == null ? "nunca pode"
								: Utils.formatExplanation(requiredExp.explain(a.required)));
				l.add(a);
			}
			dea.action = l;
		}

		// Get controle de acesso
//		if (dea.cda != null && !dea.cda.isUsuarioCorrenteAutorizado()) {
//			throw new Exception(
//					"Usuário corrente não está na lista de autorização: " + dea.cda.getListaDeAutorizados() + ".");
//		}

		// Marcas
		if (data.getEtiqueta() != null) {
			for (Etiqueta etiqueta : data.getEtiqueta()) {
				dea.tag.add(new DtoTag(etiqueta, usuario));
			}
		}

		// Mencionado por
//		Set<XrpReference> setRef = new TreeSet<XrpReference>();
//		setRef.addAll(listRef);
//		dea.mentionedBy = XrpInbox.listarReferencias(null, setRef, null, null, null);

		result.use(Results.json()).withoutRoot().from(dea).recursive().serialize();
	}

	private Usuario assertUsuarioAutorizado(Long key) {
		return null;
	}

	private static class DtoReference {
		List<DtoTag> tag = new ArrayList<>();
	}

	private static class DtoTag {
		public DtoTag(Etiqueta tag, Usuario usuario) {
			this.nome = tag.getMarcador().getNome();
			this.icon = tag.getMarcador().getIcone();
			this.titulo = Utils.calcularTempoRelativo(tag.getInicio());

			if (tag.getPessoa() != null)
				this.pessoa = tag.getPessoa().getNome();
			if (tag.getUnidade() != null)
				this.unidade = tag.getUnidade().getSigla();
			this.begin = Utils.calcularTempoRelativo(tag.getInicio());
			this.finish = Utils.calcularTempoRelativo(tag.getTermino());
			if (usuario != null) {
				if (usuario.getPessoa() != null && usuario.getPessoa().equals(tag.getPessoa()))
					this.bPessoa = true;
				if (usuario.getPapel() != null && usuario.getPapel().getUnidade() != null
						&& usuario.getPapel().getUnidade().equals(tag.getUnidade()))
					this.bUnidade = true;
			}
		}

		String pessoa;
		String unidade;
		String nome;
		String icon;
		String titulo;
		String begin;
		String finish;
		boolean bPessoa;
		boolean bUnidade;
	}

	@Get
	@Path("marcas/{key}")
	public void getTags(String key) {
//		DtoReference dtoRef = new DtoReference();
//		Key<Empresa Empresa = Key.create(key).getParent();
//		Usuario usuario = assertUsuarioAutorizado(key);
//
//		// Get data
//		//
//		XrpReference reference = dao.load(Key.create(keyEmpresa, XrpReference.class, key));
//
//		if (reference != null && reference.getTag() != null) {
//			for (XrpTag tag : reference.getTag()) {
//				dtoRef.tag.add(new DtoTag(tag, usuario));
//			}
//		}
//		result.use(Results.json()).withoutRoot().from(dtoRef).recursive().serialize();
	}

	@Delete
	@Path("dados/{key}")
	public void delete(Long key) {
		Usuario usuario = assertUsuarioAutorizado(key);
		T data = em.find(myClass, key);
		dao.remove(data);
		result.use(Results.json()).from(data, "data").recursive().serialize();
	}

	@Post
	@Path("dados")
	public void post(T data) throws Exception {
		// Usuario usuario = assertUsuarioAutorizado(data);

//		T loaded = null;
//		try {
//			Key.create(data);
//			loaded = dao.load(data);
//		} catch (IllegalArgumentException ex) {
//		}
//
//		data.onPost(validator, loaded);
//
//		if (loaded != null) {
//			// Precisamos preservar as movimentações, já que elas não podem ser
//			// corretamente reconstruídas.
//			//
//			SortedSet<IChange> event = loaded.getChange();
//			data.setChange(event);
//
//			// Se houver action filha de EditarAction, ela deve estar
//			// habilitada.
//			//
//			SortedSet<IXrpAction> actions = loaded.getXrpActions();
//			for (IXrpAction action : actions) {
//				if (action instanceof EditarAction) {
//					Expression e = action.getActive(loaded);
//					if (!e.eval()) {
//						Global juiaGlobalAction = action.getClass().getAnnotation(Global.class);
//						Global juiaGlobalData = (Global) myClass.getAnnotation(Global.class);
//
//						String s = "Ação proibida";
//						if (juiaGlobalAction != null && juiaGlobalAction.action() != null)
//							s = "Não é permitido editar est" + (juiaGlobalData.gender() == Gender.SHE ? "a" : "e") + " "
//									+ juiaGlobalData.singular().toLowerCase();
//						validator.add(new ValidationMessage(
//								s + ", motivo: " + Utils.formatExplanation(e.explain(false)) + ".", null));
//					}
//				}
//			}
//		}

		validate(data);
		dao.persistEntidade(data);

		result.use(Results.json()).from(data, "data").recursive().serialize();
	}

	private void copyPropertiesOld(T dest, T orig) throws IllegalAccessException, InvocationTargetException {
		final PropertyDescriptor[] origDescriptors = PropertyUtils.getPropertyDescriptors(orig);
		for (PropertyDescriptor origDescriptor : origDescriptors) {
			final String name = origDescriptor.getName();
			if ("class".equals(name)) {
				continue; // No point in trying to set an object's class
			}
			if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(dest, name)) {
				try {
					final Object value = PropertyUtils.getSimpleProperty(orig, name);
					BeanUtils.copyProperty(dest, name, value);
				} catch (final NoSuchMethodException e) {
					// Should not happen
				}
			}
		}
	}

	protected void validate(Object data) {
//
//		if (data != null)
//			validator.validate(data);
//		if (data instanceof XrpElementSupport)
//			((XrpElementSupport) data).onValidate(validator);
//		flushValidationError();
	}

	protected void flushValidationError() {
//		if (validator.hasErrors()) {
//			this.response.setStatus(400);
//			result.use(Results.json()).withoutRoot().from(new XrpValidationError(validator.getErrors())).recursive()
//					.serialize();
//			throw new ValidationException(null);
//		}
	}

	private static class DtoAcaoQuery {
		String confirmation;
		String html;
		IJuiaAction act;
	}

	@Get
	@Path("acao/{name}/{key}")
	public void getAcao(String name, Long key, Long idEvento) throws Exception {
		Usuario usuario = assertUsuarioAutorizado(key);

		// Get data
		//
		T data = em.find(myClass, key);
		ModeloUtils.loadProperties(data);

		IJuiaAction acao = null;
		Evento evento = null;
		if (idEvento != null)
			evento = getEvento(data, idEvento);

		// Get action from Event
		acao = getAcao(data, evento, name);
		if (acao == null)
			throw new Exception("Ação não encontrada");

		DtoAcaoQuery daq = new DtoAcaoQuery();
		acao.init(data);
		daq.confirmation = acao.getConfirmation(data, evento);
		if (!AnnotationViewBuilder.isSkipEdit(acao.getClass())) {
			AbstractHtml html = acao.getHtml();
			if (html != null)
				daq.html = html.toHtmlString();
		}
		daq.act = acao;
		result.use(Results.json()).withoutRoot().from(daq).recursive().serialize();
		return;

	}

	@Inject
	public void injectInstantiator(InstantiatorWithErrors instantiator, Validator validator) {
		this.instantiator = instantiator;
		this.validator = validator;
		validator.onErrorSendBadRequest();
	}

	private Parameters parseParameters(HttpServletRequest request) {
		Map<String, String[]> parameters = request.getParameterMap();
		List<br.com.caelum.iogi.parameters.Parameter> parameterList = new ArrayList<>(parameters.size() * 2);

		for (Map.Entry<String, String[]> param : parameters.entrySet()) {
			for (String value : param.getValue()) {
				parameterList.add(new br.com.caelum.iogi.parameters.Parameter(param.getKey(), value));
			}
		}

		return new Parameters(parameterList);
	}

	private static class AcaoResult {
		String next;
		String status;
	}

	@Post
	@Path("acao/{name}/{key}")
	public void postAcao(String name, Long key, Long idEvento, Long idEtiqueta) throws Exception {
		Usuario usuario = assertUsuarioAutorizado(key);

		// Get data
		//
		T data = em.find(myClass, key);
		ModeloUtils.loadProperties(data);

		IJuiaAction acao = null;
		Evento evento = null;
		if (idEvento != null)
			evento = getEvento(data, idEvento);

		Etiqueta etiqueta = null;
		if (idEtiqueta != null)
			etiqueta = getEtiqueta(data, idEvento, idEtiqueta);

		// Get action from Event
		acao = getAcao(data, evento, name);
		if (acao == null)
			throw new Exception("Ação não encontrada");

		// carrega os parametros do request como ParameterA anteri
		Parameters parameters = parseParameters(request);
		// cria o target para o Iogi
		Target<?> target = new Target<>(acao.getClass(), "act");
		// carega as mensagens de erro
		List<Message> errors = new ArrayList<>();
		// instancia
		Object newObject = instantiator.instantiate(target, parameters, errors);

		for (Field fld : ModeloUtils.getFieldList(acao.getClass())) {
			if (fld.getAnnotation(OneToMany.class) != null) {
				Object o = fld.get(acao);
				Object odb = em.find(o.getClass(), dao.getIdentifier(o));
				fld.set(acao, odb);
			}
		}

		// se tiver erros, carrega no validador
		validator.addAll(errors);
		Utils.copyAllNotNullFields(acao, newObject);
		acao.beforeValidate(data);

		validator.validate(acao);

		// Verifica se a ação pode ser executada
		Expression e = acao.getActive(data, null);
		if (e != null && !e.eval()) {
			Global juiaGlobal = acao.getClass().getAnnotation(Global.class);
			String s = "Ação proibida";
			if (juiaGlobal != null && juiaGlobal.action() != null)
				s = "Não é permitido " + juiaGlobal.action().toLowerCase();
			validator.add(new SimpleMessage(s + ", motivo: " + Utils.formatExplanation(e.explain(false)), null));
		}

		flushValidationError();

		try {
			acao.execute(usuario, usuario, data, evento, etiqueta);
			data.prePersistAndUpdate();
		} catch (Exception ex) {
			this.response.setStatus(400);
			if (e instanceof BadRequestException)
				throw new ValidationException(null);
			else
				throw ex;
		}
		AcaoResult ar = new AcaoResult();
		ar.next = acao.getNext();
		ar.status = "OK";
		result.use(Results.json()).withoutRoot().from(ar).recursive().serialize();
	}

//	@Get
//	@Path("miniacao/{id}/{name}/{key}")
//	public void getAcao(Long id, String name, Long key) throws Exception {
//		Usuario usuario = assertUsuarioAutorizado(key);
//		T data = em.find(myClass, key);
//		ModeloUtils.loadProperties(data);
//
//		SortedSet<IJuiaAction> xrpActions = data.getActions();
//		Evento xrpc = getEvento(data, id);
//		IJuiaAction xrpma = getAcao(xrpc, name);
//
//		DtoAcaoQuery daq = new DtoAcaoQuery();
//		daq.confirmation = xrpma.getConfirmation(data, xrpc);
//		result.use(Results.json()).withoutRoot().from(daq).recursive().serialize();
//	}

	private IJuiaAction getAcao(T entidade, Evento evento, String name) {
		if (evento != null) {
			for (Acao a : (SortedSet<Acao>) evento.getMiniActions()) {
				if (name.equals(a.getName()))
					return a;
			}
		} else {
			// Get action from Entity
			SortedSet<IJuiaAction> xrpActions = entidade.getActions();
			if (xrpActions != null) {
				for (IJuiaAction a : xrpActions) {
					if (!name.equals(a.getName()))
						continue;
					return a;
				}
			}
		}

		return null;
	}

	protected Evento<?, ?> getEvento(T data, Long id) {
		for (Evento<?, ?> e : data.getEvento()) {
			if (id.equals(e.getId()))
				return e;
		}
		return null;
	};

	protected Etiqueta getEtiqueta(T data, Long idEvento, Long idEtiqueta) {
		return em.find(Etiqueta.class, idEtiqueta);
	};

//	@Post
//	@Path("miniacao/{id}/{name}/{key}")
//	public void postAcao(Long id, String name, Long key) throws Exception {
//		Usuario usuario = assertUsuarioAutorizado(key);
//		T data = em.find(myClass, key);
//		ModeloUtils.loadProperties(data);
//
//		SortedSet<IJuiaAction> xrpActions = data.getActions();
//		Evento xrpc = getEvento(data, id);
//		IJuiaAction xrpma = getAcao(xrpc, name);
//
//		xrpma.execute(usuario, usuario, data, xrpc);
//		result.use(Results.status()).ok();
//	}

//	private IXrpMiniAction getXrpMiniAction(IXrpChange xrpc, String name) throws Exception {
//		IXrpMiniAction xrpma = null;
//		SortedSet<IXrpMiniAction> lma = xrpc.getXrpMiniActions();
//		if (lma == null)
//			throw new Exception("Nenhuma mini-ação na lista.");
//
//		for (IXrpMiniAction xrpa : lma) {
//			if (!name.equals(xrpa.getName()))
//				continue;
//			xrpma = xrpa;
//		}
//		if (xrpma == null)
//			throw new Exception("Mini-ação não encontrada na lista.");
//		return xrpma;
//	}
//
//	private IXrpChange getXrpChange(T data, String id) throws Exception {
//		// Get event
//		IXrpChange xrpc = null;
//		if (data.getChange() == null)
//			throw new Exception("Nenhuma movimentação na lista.");
//		for (IChange ch : data.getChange()) {
//			IXrpChange c = (IXrpChange) ch;
//			if (!id.equals(c.getId()))
//				continue;
//			xrpc = c;
//			break;
//		}
//		if (xrpc == null)
//			throw new Exception("Movimentação não encontrada na lista.");
//		return xrpc;
//	}

	@Get
	@Path("buscar/{string}")
	public void buscar(String string) {
//		ContextInterceptor.assertUsuarioCorrente();
//
//		List<T> l = lerReferencias(string);
//		List<RefOriginalObject> l2 = new ArrayList<>();
//		for (T o : l) {
//			// RefOriginalObject r = new RefOriginalObject(dao.webSafeKey(o),
//			// o.getSelectFirstLine(), o.getSelectSecondLine());
//			RefOriginalObject r = new RefOriginalObject(dao.webSafeKey(o), null, null, null, null, null,
//					o.getSelectFirstLine(), o.getSelectSecondLine());
//			l2.add(r);
//		}
//		result.use(Results.json()).from(l2).recursive().serialize();
	}

//	public List<T> lerReferencias(String string) {
//		List<T> l = buscarReferencias(myClass, dao, string);
//		return l;
//	}

//	public static <C> List<C> buscarReferencias(Class<C> myClass, Dao dao, String string) {
//		Usuario usuario = ContextInterceptor.assertUsuarioCorrente();
//		@OneToMany Empresa> refEmpresa = usuario.getRefEmpresa();
//		if (refEmpresa == null)
//			throw new XrpBadRequestException("Usuário precisa estar vinculado a uma empresa para buscar");
//		List<C> l = new ArrayList<>();
//		if (string != null && string.trim().length() > 0) {
//			string = string.trim().replaceAll("\\s+", " ");
//
//			l = buscarEmEmpresa(myClass, dao, string, refEmpresa);
//
//			// Busca em outra empresa
//			if (false) {
//				if (l.size() == 0) {
//					String[] a = string.split(" ");
//					if (a.length > 1) {
//						String idEmpresa = a[0];
//						Empresa emp = dao.selectFirstByCondition(Empresa.class, "identificador", a[0]);
//						if (emp != null) {
//							l = buscarEmEmpresa(myClass, dao, string.substring(a[0].length() + 1), Ref.create(emp));
//						}
//					}
//				}
//			}
//		}
//		return l;
//	}
//
//	public static <C> List<C> buscarEmEmpresa(Class<C> myClass, Dao dao, String string, @OneToMany Empresa> refEmpresa) {
//		List<C> l;
//		// Busca principal
//		l = dao.selectByIndexByAncestor(XrpReference.canonicalizar(string), myClass, refEmpresa.getKey());
//
//		if (l.size() == 0) {
//			if (refEmpresa != null) {
//				// Acrescenta o nome da empresa
//				l = dao.selectByIndexByAncestor(
//						XrpReference.canonicalizar(refEmpresa.get().getIdentificador() + string), myClass,
//						refEmpresa.getKey());
//				// Acrescenta o ano corrente
//				if (l.size() == 0) {
//					l = dao.selectByIndexByAncestor(
//							XrpReference.canonicalizar(
//									refEmpresa.get().getIdentificador() + (Utils.newDate().getYear() + 1900) + string),
//							myClass, refEmpresa.getKey());
//				}
//			}
//		}
//		return l;
//	}
//
//	@Get
//	@Path("selecionar")
//	public void selecionar() throws Exception {
//		ContextInterceptor.assertUsuarioCorrente();
//
//		List<T> l = lerTodos();
//		List<RefDTO> l2 = new ArrayList<>();
//		for (T i : l) {
//			if (i instanceof Modelo) {
//				Modelo o = (Modelo) i;
//				Empresa empresa = o.getRefEmpresa().get();
//				RefDTO r = new RefDTO(Key.create(o).toWebSafeString(), o.getTitle(), o.getDescr(), o.getCode(),
//						empresa == null ? null : empresa.getCode(), empresa == null ? null : empresa.getNome(),
//						o.getSelectFirstLine(), o.getSelectSecondLine());
//				l2.add(r);
//			}
//		}
//		result.use(Results.json()).from(l2).recursive().serialize();
//	}
//
	@Get
	@Path("todos")
	public void todos() throws Exception {
		List<Field> fields = getSearchFields();
		JSONObject res = new JSONObject();
		JSONArray a = new JSONArray();
		res.put("list", a);

		List<T> l = lerTodos(null, null, null, null, null, null).list;

		for (T t : l) {
			JSONObject o = new JSONObject();
			o.put("key", emf.getPersistenceUnitUtil().getIdentifier(t));
			for (Field f : fields) {
				Object value = f.get(t);
				if (value == null)
					continue;
				if (IEnum.class.isAssignableFrom(f.getType()))
					value = ((IEnum) value).getDescr();
				else if (IEntidade.class.isAssignableFrom(f.getType()))
					value = ((IEntidade) value).getDescr();
				else if (Boolean.class.isAssignableFrom(f.getType()) || "boolean".equals(f.getType().toString()))
					value = (Boolean) value ? "Sim" : "Não";
				else if (Date.class.isAssignableFrom(f.getType()))
					value = Utils.formatDDMMYYYY((Date) value);
				else if (LocalDate.class.isAssignableFrom(f.getType()))
					value = value != null ? ((LocalDate) value).toString("dd/MM/yyyy") : null;
				o.put(f.getName(), value);
			}
			a.put(o);
		}
		result.use(Results.http()).addHeader("Content-Type", "application/json").body(res.toString(2));
	}

	protected CriteriaBuilder cb() {
		return em.getCriteriaBuilder();
	}

	protected static class LerTodos<T> {
		Long totalRow;
		List<T> list;
	}

	public LerTodos<T> lerTodos(Integer pageSize, Integer pageNumber, String searchKey, String searchValue,
			String queryKey, String queryValue) {
		Global juiaGlobal = (Global) myClass.getAnnotation(Global.class);

		LerTodos<T> res = new LerTodos<>();

		CriteriaQuery<Long> cq = cb().createQuery(Long.class);
		Root<T> cqr = cq.from(myClass);
		cq.select(cb().count(cqr));
		cq.where(lerTodosPredicates(cqr, searchKey, searchValue, queryKey, queryValue));
		res.totalRow = em.createQuery(cq).getSingleResult();

		CriteriaQuery<T> q = cb().createQuery(myClass);
		Root<T> c = q.from(myClass);
		q.select(c);
//		Join<DpPessoa, CpOrgaoUsuario> joinOrgao = c.join("orgaoUsuario", JoinType.INNER);

		q.where(lerTodosPredicates(c, searchKey, searchValue, queryKey, queryValue));
//		if (ordemDesc) {
//			q.orderBy(cb().desc(c.get("dataInicioPessoa")));
//		} else {
//			q.orderBy(cb().asc(c.get("dataInicioPessoa")));
//		}

		List<T> l = em.createQuery(q)
				.setFirstResult((pageNumber != null && pageSize != null) ? (pageNumber - 1) * pageSize : 0)
				.setMaxResults(pageSize != null ? pageSize : Integer.MAX_VALUE).getResultList();

//		if (juiaGlobal != null && juiaGlobal.inactivable()) {
//			List<T> l = dao.loadAllByConditionByAncestor(myClass, "active", true, usuario.getRefEmpresa().getKey());
//			if (Comparable.class.isAssignableFrom(myClass))
//				Collections.sort((List<Comparable>) l);
//			return l;
//		}

		if (Comparable.class.isAssignableFrom(myClass))
			Collections.sort((List<Comparable>) l);
		res.list = l;
		return res;
	}

	protected Predicate[] lerTodosPredicates(Root<T> c, String searchKey, String searchValue, String queryKey,
			String queryValue) {
		List<Predicate> whereList = new LinkedList<Predicate>();
		if (searchKey != null && searchValue != null) {
			whereList.add(cb().equal(c.get("key".equals(searchKey) ? Entidade.Fields.id : searchKey), searchValue));
		}
		if (queryKey != null && queryValue != null) {
			whereList.add(cb().equal(c.get(queryKey), queryValue));
		}
		whereList.add(cb().isNull(c.get(Entidade.Fields.termino)));
//		whereList.add(cb().equal(joinOrgao.get("idOrgaoUsu"), idOrgaoUsu));
//		if (matricula != null) {
//			whereList.add(cb().equal(c.get("matricula"), matricula));
//		}
//		if (pessoasFinalizadas) {
//			whereList.add(cb().isNotNull(c.get("dataFimPessoa")));
//		} else {
//			whereList.add(cb().isNull(c.get("dataFimPessoa")));
//		}
		Predicate[] whereArray = new Predicate[whereList.size()];
		whereList.toArray(whereArray);
		return whereArray;
	}

	protected List<Field> getSearchFields() {
		return com.crivano.juia.util.JuiaUtils.getSearchFields(myClass);
	}

	protected Div templateExibir(Class<T> clazz) throws Exception {

		Div div = HtmlTemplateBuilder.build("data.", clazz.newInstance(), Kind.ShowView);

		// Acrescentar modal de ação
//		CustomTag validationObserver = new CustomTag("validation-observer", div,
//				new CustomAttribute("v-slot", "{ invalid }"));
		CustomTag modal = new CustomTag("b-modal", div, new Id("action"), new CustomAttribute("size", "xl"),
				new CustomAttribute(":title", "actTitle"), new CustomAttribute("@ok", "actOK"),
				new CustomAttribute("hide-footer"),
				// new CustomAttribute(":ok-disabled", "actInvalid"),
				new CustomAttribute("ref", "actionModal"));

//		Div modalDiv = new Div(div, new CustomAttribute("v-if", "actShow"));
//		CustomTag modalTransition = new CustomTag("transition", modalDiv, new Name("modal"));
//		Div modalMask = new Div(modalTransition, new ClassAttribute("modal-mask"));
//		Div modalWrapper = new Div(modalMask, new ClassAttribute("modal-wrapper"));
//		Div modalDialog = new Div(modalWrapper, new ClassAttribute("modal-dialog"));
//		Div modalContent = new Div(modalDialog, new ClassAttribute("modal-content"));
//		Div modalHeader = new Div(modalContent, new ClassAttribute("modal-header"));
//		H4 modalTitle = new H4(modalHeader, new ClassAttribute("modal-title"));
//		new NoTag(modalTitle, "Modal title");
//		Button modalClose = new Button(modalHeader, new Type("button"), new ClassAttribute("close"),
//				new CustomAttribute("click", "actShow=false"));
//		Span modalButtonSpan = new Span(modalClose, new CustomAttribute("aria-hidden", "true"));
//		new NoTag(modalButtonSpan, "&times;");
//		Div modalBody = new Div(modalContent, new ClassAttribute("modal-body"));
//		Div modalFooter = new Div(modalContent, new ClassAttribute("modal-footer"));
//
//		Button modalCancel = new Button(modalFooter, new Type("button"), new ClassAttribute("btn btn-secondary"),
//				new CustomAttribute("data-dismiss", "modal"), new CustomAttribute("click", "actShow=false"));
//		new NoTag(modalCancel, "Cancelar");
//
//		Button modalOK = new Button(modalFooter, new Type("button"), new ClassAttribute("btn btn-primary"),
//				new CustomAttribute("data-dismiss", "modal"),
//				new CustomAttribute("click", "actShow=false"));
//		new NoTag(modalOK, "OK");

		Div component = new Div(modal, new CustomAttribute(":is",
				"{template:actTemplate, name:'ActionForm', data() { return {act:{}}}, methods: {proxify: function (v) {if (v === undefined) v = this.act;for (var k in v) {if (Object.prototype.hasOwnProperty.call(v, k)) {var val = v[k];delete v[k];this.$set(v, k, val);}}}}}"),
				new CustomAttribute("ref", "action"), new CustomAttribute("@keydown.enter.native.prevent", "actEnter"));

		// Acrescentar a lista de ações
		{
			Div row = (Div) JuiaUtils.findChildByAttribute(div, new Id("sidebar"));

			List<AbstractHtml> children = row.getChildren();
			row.removeAllChildren();

			String[] attrs = { "v-if=action.length>0" };

			// Título da barra de ações
			Div col = HtmlTemplateBuilder.drawGroupHeader(row, "Ações", attrs);

			// Botões
			Ul ul = new Ul(col, new ClassAttribute("list-unstyled", "blog-tags", "xrp-actions"));
			Li li = new Li(ul, new CustomAttribute("v-for", "act in action"),
					new CustomAttribute("v-if", "act.active || audit"),
					new CustomAttribute("@click.prevent", "act.active ? $eval(act.click) : null"),
					new CustomAttribute(":class",
							"{'blog-tags-enabled-true': act.active, 'blog-tags-enabled-false': !act.active, 'blog-tags-required': act.required, 'blog-tags-on': act.slug == 'auditar' && audit}"),
					new CustomAttribute("v-b-popover.hover.top", "act.explanation"),
					new CustomAttribute(":id", "'action-' + act.slug"));
			I ii = new I(li, new CustomAttribute(":class", "act.icon"));
			NoTag noTag2 = new NoTag(li, " {{act.name}}");

			row.appendChildren(children);
		}

		// Acrescentar a lista de Pendências
		{
			Div row = (Div) JuiaUtils.findChildByAttribute(div, new Id("sidebar"));

			List<AbstractHtml> children = row.getChildren();
			row.removeAllChildren();

			String[] attrs = { "v-if=required.length>0" };

			// Título da barra de ações
			Div col = HtmlTemplateBuilder.drawGroupHeader(row, "Pendências", attrs);

			// Botões
			Ul ul = new Ul(col, new ClassAttribute("list-required", "margin-bottom-0", "fa-ul", "ml-4"));
			Li li = new Li(ul, new CustomAttribute("v-for", "r in required"),
					new CustomAttribute("@click.prevent", "actQuery(r.action.name, r.event ? r.event.id : undefined)"),
					new CustomAttribute("v-b-popover.hover.top", "r.action.requiredExplanation"),
					new CustomAttribute(":id",
							"'required' + (r.event ? (r.event.slug ? '-' + r.event.slug : '-' + r.event.id) : '') + (r.action ? '-' + r.action.slug : '')")
			// new CustomAttribute("v-if", "t.bPessoa || t.bUnidade")
			);
			A a = new A(li);
			I ii = new I(a, new CustomAttribute(":class", "r.action.icon"), new ClassAttribute("fa-li"));
			NoTag noTag2 = new NoTag(a,
					" {{((r.event && r.event.titulo) ? r.event.titulo + ': ' : '') + r.action.name}}");

			row.appendChildren(children);
		}

		// Acrescentar a lista de Etiquetas
		{
			Div row = (Div) JuiaUtils.findChildByAttribute(div, new Id("sidebar"));

			List<AbstractHtml> children = row.getChildren();
			row.removeAllChildren();

			String[] attrs = { "v-if=tag.length>0" };

			// Título da barra de ações
			Div col = HtmlTemplateBuilder.drawGroupHeader(row, "Etiquetas", attrs);

			// Botões
			Ul ul = new Ul(col, new ClassAttribute("list-inline", "margin-bottom-0"));
			Li li = new Li(ul, new CustomAttribute("v-for", "t in tag")
			// new CustomAttribute("v-if", "t.bPessoa || t.bUnidade")
			);
			A a = new A(li, new ClassAttribute("btn btn-light xrp-label"));
			I ii = new I(a, new CustomAttribute(":class", "t.icon"));
			NoTag noTag2 = new NoTag(a, " {{t.nome}}");

			row.appendChildren(children);
		}

		// Acrescentar as movimentações
		{
			Div row = (Div) JuiaUtils.findChildByAttribute(div, new Id("content"));

			String[] attrs = { "v-if=filteredEvents && filteredEvents.length>0" };
			Div col = HtmlTemplateBuilder.drawGroupHeader(row, "Eventos", attrs);

			// Div col = new Div(row, new ClassAttribute("col", "col-12"));

			Table table = new Table(col, new ClassAttribute("table table-striped table-sm"));
			THead thead = new THead(table);
			Tr trh = new Tr(thead);
			new NoTag(new Th(trh), "Tempo");
			new NoTag(new Th(trh), "Responsável");
			new NoTag(new Th(trh), "Tipo");
			new NoTag(new Th(trh), "Descrição");
			new NoTag(new Th(trh), "");

			TBody tbody = new TBody(table);
			Tr trb = new Tr(tbody, new CustomAttribute("v-for", "e in filteredEvents"), new CustomAttribute(":class",
					"{'event-inactive': e.tipo === 'Cancelamento' || e.idCanceladora !== undefined || e.idDesabilitadora !== undefined}"));
			new NoTag(new Td(trb),
					"<span :title=\"e.dt.substring(0,10) + ' ' + e.dt.substring(11,19)\">{{e.tempoRelativo}}</span>");
			new NoTag(new Td(trb), "{{e.agente}}");
			new NoTag(new Td(trb), "{{e.tipo}}");
			new NoTag(new Td(trb, new CustomAttribute("v-html", "e.descr")));
			// new Span(new Td(trb), new ClassAttribute(
			// "btn-u btn-u-default btn-u-xs"), new Type("button")) {
			// {
			// new I(this, new ClassAttribute("fa fa-trash-o"));
			// }
			// };
			new Ul(new Td(trb, new Style("text-align:center;")), new ClassAttribute("list-unstyled blog-tags"),
					new Style("margin-bottom: 0px")) {
				{
					new Li(this, new CustomAttribute("v-for", "act in e.action"),
							// new CustomAttribute("@click.prevent", "actQuery(act.name, e.id)"),
							new CustomAttribute("@click.prevent", "act.active ? $eval(act.click) : null"),
							new CustomAttribute(":class", "'blog-tags-enabled-' + act.active"),
							new Style("margin-bottom: 0px;padding-bottom:0px;;padding-top:0px;")) {
						{
							new I(this, new CustomAttribute(":class", "act.icon"), new CustomAttribute(":title",
									"actMiniTitle(act.name, act.active, act.explanation)"));
						}
					};
				}
			};
			new CustomTag("dir-pagination-controls", col, new CustomAttribute("max-size", "9"),
					new CustomAttribute("direction-links", "true"), new CustomAttribute("boundary-links", "true"));
		}

		// Acrescentar a lista de referenciado por
		// Nato: Temporariamente desabilitado
		if (false) {
			Div row = (Div) JuiaUtils.findChildByAttribute(div, new Id("content"));

			String[] attrs = { "v-if=mentionedBy" };
			Div col = HtmlTemplateBuilder.drawGroupHeader(row, "Referenciado Por", attrs);

			// Div col = new Div(row, new ClassAttribute("col", "col-12"));

			Table table = new Table(col, new ClassAttribute("table table-striped table-sm"));
			THead thead = new THead(table);
			Tr trh = new Tr(thead);
			new NoTag(new Th(trh), "Tempo");
			new NoTag(new Th(trh), "Tipo");
			new NoTag(new Th(trh), "Código");
			new NoTag(new Th(trh), "Descrição");
			new NoTag(new Th(trh), "Etiquetas");

			TBody tbody = new TBody(table);
			Tr trb = new Tr(tbody,
					new CustomAttribute("dir-paginate", "mention in mentionedBy | filter: filter | itemsPerPage:10"),
					new CustomAttribute("@click.prevent", "navigate(mention.link)"));
			new NoTag(new Td(trb),
					"<span title=\"{{mention.dt.substring(0,10)}} {{mention.dt.substring(11,19)}}\">{{mention.tempoRelativo}}</span>");
			new NoTag(new Td(trb), "{{mention.tipo}}");
			new NoTag(new Td(trb), "{{mention.code}}");
			new NoTag(new Td(trb), "{{mention.descr}}");

			new Ul(new Td(trb), new ClassAttribute("list-inline tags-v2"), new Style("margin-bottom: 0px")) {
				{
					new Li(this, new CustomAttribute("v-for", "m in mention.marcador"),
							new ClassAttribute("blog-tags-enabled-true"),
							new Style("margin-bottom: 0px;padding-bottom:0px;;padding-top:0px;")) {
						{
							new I(this, new ClassAttribute("{{m.icon}}"), new Title("{{m.nome}}"));
						}
					};

				}
			};
			new CustomTag("dir-pagination-controls", col, new CustomAttribute("max-size", "9"),
					new CustomAttribute("direction-links", "true"), new CustomAttribute("boundary-links", "true"));
		}

		return div;
	}

	protected Div templateEditar(Class<T> clazz) throws Exception {
		Div all = HtmlTemplateBuilder.build("data.", clazz.newInstance(), Kind.EditView);
		System.out.println(all.toHtmlString());
		Form p = (Form) JuiaUtils.findChildByAttribute(all, new CustomAttribute("id", "form"));
		Div div = (Div) p.getParent();
		List<AbstractHtml> children = div.getChildren();
		div.removeAllChildren();
		CustomTag validationObserver = new CustomTag("validation-observer", div,
				new CustomAttribute("v-slot", "{ invalid }"));
		new CustomTag("norma-show", div, new CustomAttribute(":data", "data"));
		validationObserver.appendChildren(children);

		Button save = (Button) JuiaUtils.findChildByAttribute(all, new CustomAttribute("@click.prevent", "save()"));
		save.addAttributes(new CustomAttribute(":disabled", "invalid"));
		return all;
	}

	protected Div templateListar(Class<T> clazz) throws Exception {
		return HtmlTemplateBuilder.build("data.", clazz.newInstance(), Kind.SearchView);
	}

	private static class JuiaInfo {
		boolean edit;
		boolean show;
	}

	@Get
	@Path("juia/info")
	public void juiaInfo() throws Exception {
		JuiaInfo ji = new JuiaInfo();
		List<Field> editFields = JuiaUtils.getEditFields(myClass);
		ji.edit = editFields.size() > 0;
		if (editFields.size() == 1 && editFields.get(0).getAnnotation(javax.persistence.Id.class) != null)
			ji.edit = false;
		ji.show = true;
		result.use(Results.json()).withoutRoot().from(ji).recursive().serialize();
	}

	@Get
	@Path("juia/selectpage")
	public void juiaSelectPage(Integer pageSize, Integer pageNumber, String searchKey, String searchValue,
			String queryKey, String queryValue) throws Exception {
		LerTodos<T> l = lerTodos(pageSize, pageNumber, searchKey, searchValue, queryKey, queryValue);
		result.use(Results.json()).withoutRoot().from(l).recursive().serialize();
	}

	@Get
	@Path("juia/exibir.vue")
	public void htmlExibir() throws Exception {
		Div div = templateExibir((Class<T>) myClass);
		String s = div.toHtmlString();
		resultVue(s, "show");
	}

	@Get
	@Path("juia/editar.vue")
	public void htmlEditar() throws Exception {
		Div div = templateEditar((Class<T>) myClass);
		String s = div.toHtmlString(StandardCharsets.UTF_8);
		resultVue(s, "edit");
	}

	@Get
	@Path("juia/listar.vue")
	public void htmlListar() throws Exception {
		Div div = templateListar((Class<T>) myClass);
		String s = div.toHtmlString();
		resultVue(s, "list");
	}

	private void resultVue(String template, String kind) {
		Document doc = Jsoup.parseBodyFragment(template);
		template = doc.body().html().toString();
		template = template.replace("dir-paginate=\"data in list | filter: filter | itemsPerPage:20\"",
				"v-for=\"data in list\"");

		String script = "module.exports = {\n";
		script += "  name: '__LOCATOR_SLUG_____KIND__',\n";
		script += "  mixins: [window.juia.__KIND__, window.juia.__LOCATOR_SLUG_____KIND__ || {}],\n";
		script += "  data() {\n";
		script += "  	return {\n";
		script += "  		locator: '__LOCATOR__',\n";
		script += "  		clazz: '__CLASS__',\n";
		if ("list".equals(kind))
			script += "  		filter: undefined,\n";
		script += "  	}\n";
		script += "  }\n";
		script += "}";

		String locator = myClass.getAnnotation(Global.class).locator();
		String locatorSlug = Texto.slugify(locator, true, true);
		script = script.replace("__LOCATOR__", locator);
		script = script.replace("__LOCATOR_SLUG__", locatorSlug);
		script = script.replace("__KIND__", kind);
		script = script.replace("__CLASS__", myClass.getSimpleName());

		String vue = "<template>\n" + template + "\n</template>\n<script>\n" + script
				+ "\n</script>\n<style scoped>\n</style>";
		result.use(Results.http()).addHeader("Content-Type", "text/html").body(vue);
	}

	@SuppressWarnings("unchecked")
	protected Class<T> getMyClass() {
		Class thiz = CDIProxies.extractRawTypeIfPossible(this.getClass());
		Class<T> clazz = (Class<T>) ((ParameterizedType) thiz.getGenericSuperclass()).getActualTypeArguments()[0];
		return clazz;
	}

	protected boolean isVersionable() {
		Global global = getMyClass().getAnnotation(Global.class);
		if (global == null)
			return false;
		return global.versionable();
	}
}
