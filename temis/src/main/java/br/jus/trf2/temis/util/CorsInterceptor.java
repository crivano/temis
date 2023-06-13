package br.jus.trf2.temis.util;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import lombok.NoArgsConstructor;

//@Intercepts(before = TemisInterceptor.class)
//@RequestScoped
@NoArgsConstructor
public class CorsInterceptor {
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Inject
	public CorsInterceptor(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		try {
			String origin = request.getHeader("Origin");
			if (origin == null)
				origin = "*";
			response.addHeader("Access-Control-Allow-Origin", origin);
			response.addHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type,Authorization");
			response.addHeader("Access-Control-Allow-Credentials", "true");
			stack.next();
		} catch (Exception e) {
			throw new InterceptionException(e);
		} finally {
		}
	}

	public boolean accepts(ControllerMethod method) {
		return true; // Will intercept all requests
	}
}