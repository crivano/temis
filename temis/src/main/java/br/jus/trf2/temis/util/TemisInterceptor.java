package br.jus.trf2.temis.util;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.crivano.swaggerservlet.LogResponse;
import com.crivano.swaggerservlet.SwaggerAuthorizationException;
import com.crivano.swaggerservlet.SwaggerError;
import com.crivano.swaggerservlet.SwaggerUtils;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.jpa.JPATransactionInterceptor;
import br.com.caelum.vraptor.validator.Validator;
import br.jus.trf2.temis.api.v1.TemisApiV1Servlet;
import br.jus.trf2.temis.core.util.ContextoPersistencia;

@RequestScoped
@Intercepts(before = JPATransactionInterceptor.class)
public class TemisInterceptor {
	private static final Logger log = LoggerFactory.getLogger(TemisInterceptor.class);

	private final EntityManager manager;
	private final Validator validator;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final ServletContext context;

	/**
	 * @deprecated CDI eyes only
	 */
	public TemisInterceptor() {
		super();
		manager = null;
		validator = null;
		request = null;
		response = null;
		context = null;
	}

	@Inject
	public TemisInterceptor(EntityManager manager, Validator validator, ServletContext context,
			HttpServletRequest request, HttpServletResponse response) {
		this.manager = manager;
		this.validator = validator;
		this.request = request;
		this.response = response;
		this.context = context;
	}

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) throws Throwable {

//		ModeloDao.freeInstance();
		try {
			ContextoPersistencia.setEntityManager(this.manager);

			// Inicialização padronizada
//			CurrentRequest.set(new RequestInfo(this.context, this.request, this.response));

			try {
				// Wf.getInstance().getConf().limparCacheSeNecessario();
			} catch (Exception e1) {
				throw new RuntimeException("Não foi possível atualizar o cache de configurações", e1);
			}

			validator.onErrorSendBadRequest();

//			try {
//				ContextoPersistencia
//						.setUserPrincipal(AutenticadorFabrica.getInstance().obterPrincipal(request, response));
//			} catch (Exception e) {
//				throw new SwaggerAuthorizationException(e);
//			}

			stack.next();
		} catch (Exception e) {
			int sts = 500;
			if (e instanceof SwaggerAuthorizationException)
				sts = 401;
			String errorcode = TemisApiV1Servlet.INSTANCE.errorCode(e);

			SwaggerError error = SwaggerUtils.writeJsonError(sts, errorcode, request, response, e, null, null,
					"vraptor-call", null, ContextoPersistencia.getUserPrincipal(), null);
			response.getWriter().close();

			if (TemisApiV1Servlet.INSTANCE.shouldBeLogged(sts, e)) {
				LogResponse lr = new LogResponse();
				lr.method = request.getMethod();
				lr.path = request.getContextPath() + request.getPathInfo();
				lr.request = null;
				lr.response = error;
				String details = SwaggerUtils.toJson(lr);

				log.error("HTTP-ERROR: {}, EXCEPTION {}", details,
						SwaggerUtils.simplifyStackTrace(e, TemisApiV1Servlet.errorPackages));
			}
			throw e;
		} finally {
			ContextoPersistencia.removeUserPrincipal();
			ContextoPersistencia.setEntityManager(null);
		}
	}

	public boolean accepts(ControllerMethod method) {
		return true; // Will intercept all requests
	}

}