package br.jus.trf2.temis.core.util;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crivano.swaggerservlet.SwaggerUtils;

@SuppressWarnings("serial")
public class ErrorHandlerServlet extends HttpServlet {

	// Method to handle GET method request.
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		String servletName = (String) request.getAttribute("javax.servlet.error.servlet_name");
		String newWindow = (String) request.getParameter("newWindow");
		if ("1".equals(newWindow))
			request.setAttribute("newWindow", newWindow);

		if (servletName == null) {
			servletName = "Unknown";
		}
		String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");

		if (requestUri == null) {
			requestUri = "Unknown";
		}

		if (response.isCommitted())
			return;

		request.setAttribute("exception", throwable);

		Throwable t = throwable;
		if (t != null) {
			while ((t.getClass().getSimpleName().equals("ServletException")
					|| t.getClass().getSimpleName().equals("InterceptionException")
					|| t.getClass().getSimpleName().equals("InvocationTargetException")
					|| t.getClass().getSimpleName().equals("ProxyInvocationException")) && t.getCause() != null
					&& t != t.getCause()) {
				t = t.getCause();
			}

			request.setAttribute("exceptionApp", false);
			request.setAttribute("exceptionMsg", t.getMessage());
			request.setAttribute("exceptionGeral", t);
			String stackTrace = simplificarStackTrace(t);
			request.setAttribute("exceptionStackGeral", stackTrace);
			SwaggerUtils.log(ErrorHandlerServlet.class).error(stackTrace);
		}

		// request.getRequestDispatcher("/WEB-INF/erroGeral.jsp").forward(request,
		// response);
		response.addHeader("Access-Control-Allow-Origin", "*");
		SwaggerUtils.writeJsonError(400, ((Exception) t).getMessage(), request, response, (Exception) t, null, null,
				null, null, null, null);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static String[] packages = { "br.jus.trf2", "com.crivano" };

	public static String simplificarStackTrace(Throwable t) {
		if (t == null)
			return null;

		while ((t.getClass().getSimpleName().equals("ServletException")
				|| t.getClass().getSimpleName().equals("InterceptionException")
				|| t.getClass().getSimpleName().equals("InvocationTargetException")
				|| t.getClass().getSimpleName().equals("ProxyInvocationException")) && t.getCause() != null
				&& t != t.getCause()) {
			t = t.getCause();
		}

		// while (t.getCause() != null && t != t.getCause()) {
//			if (t.getClass().getSimpleName().equals("AplicacaoException"))
//				break;
//			t = t.getCause();
//		}

//		StackSimplier ss = new StackSimplier();
//		String s = ss.simplify(t);

//		java.io.StringWriter sw = new java.io.StringWriter();
//		java.io.PrintWriter pw = new java.io.PrintWriter(sw);
//		t.printStackTrace(pw);
//		String s = sw.toString();

		String s = simplifyStackTrace(t, packages);
		return s.trim();
	}

	public static String simplifyStackTrace(Throwable t, String[] pkgs) {
		if (t == null)
			return null;
		java.io.StringWriter sw = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(sw);
		t.printStackTrace(pw);
		String s = sw.toString();
		if (true) {
			StringBuilder sb = new StringBuilder();
			String[] lines = s.split(System.getProperty("line.separator"));
			for (int i = 0; i < lines.length; i++) {
				String l = lines[i];
				boolean isInPackages = false;
				if (pkgs != null) {
					for (String pkg : pkgs) {
						isInPackages |= l.contains(pkg);
					}
				}
				if (!l.startsWith("\t") || (isInPackages && !l.contains("$$_Weld") && !l.contains(".invoke(")
						&& !l.contains(".doFilter("))) {
					sb.append(l);
					sb.append(System.getProperty("line.separator"));
				}
			}
			s = sb.toString();
		}
		return s;
	}

}