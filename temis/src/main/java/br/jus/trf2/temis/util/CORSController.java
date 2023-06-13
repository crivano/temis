package br.jus.trf2.temis.util;

import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Options;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.view.Results;

//@Controller
public class CORSController {

	@Inject
	private Result result;

	@Inject
	private Router router;

	@Inject
	private HttpServletRequest requestInfo;

	@Options("/*")
	public void options() {

		Set<HttpMethod> allowed = router.allowedMethodsFor(requestInfo.getRequestURI());

		String allowMethods = allowed.toString().replaceAll("\\[|\\]", "");

		result.use(Results.status()).header("Allow", allowMethods);

		result.use(Results.status()).header("Access-Control-Allow-Methods", allowMethods);

		result.use(Results.status()).header("Access-Control-Allow-Headers",
				"Content-Type, accept, Authorization, X-Tenant, X-Filial, origin");

		result.use(Results.status()).noContent();

	}
}