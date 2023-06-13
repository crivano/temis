package br.jus.trf2.temis.core.util;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Intercepts
@RequestScoped
@NoArgsConstructor
public class ContextInterceptor {

	@Data
	@AllArgsConstructor
	public static class Context {
		HttpServletRequest request;
		HttpServletResponse response;
		Dao dao;
	}

	private static ThreadLocal<Context> context = new ThreadLocal<>();

	@Inject
	public ContextInterceptor(HttpServletRequest request, HttpServletResponse response, Dao dao) {
		context.set(new Context(request, response, dao));
	}

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		stack.next();
		context.set(null);
	}

	public boolean accepts(ControllerMethod method) {
		return true; // Will intercept all requests
	}

	public static Context getContext() {
		return context.get();
	}

	public static Dao getDao() {
		return getContext().getDao();
	}
}