package br.jus.trf2.temis.api.v1;

import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletConfig;

import com.crivano.swaggerservlet.SwaggerContext;
import com.crivano.swaggerservlet.SwaggerServlet;
import com.crivano.swaggerservlet.dependency.TestableDependency;

import br.jus.trf2.temis.core.util.ContextoPersistencia;
import br.jus.trf2.temis.core.util.Prop;
import br.jus.trf2.temis.core.util.Prop.IPropertyProvider;

// @WebServlet(value = "/api/v1", loadOnStartup = 1)
public class TemisApiV1Servlet extends SwaggerServlet implements IPropertyProvider {
	private static final long serialVersionUID = 1756711359239182178L;

	public static TemisApiV1Servlet INSTANCE = null;

	public static String[] errorPackages = new String[] { "br.jus.trf2", "br.gov.jfrj", "com.crivano" };

	@Override
	public void initialize(ServletConfig config) throws Exception {
		super.initialize(config);
		INSTANCE = this;
		setAPI(ITemisApiV1.class);

		setActionPackage("br.jus.trf2.temis.api.v1");
		for (String ep : errorPackages)
			showPackageErrors(ep);

		Prop.setProvider(this);
		Prop.defineGlobalProperties();
		defineProperties();

		class HttpGetDependency extends TestableDependency {
			String testsite;

			HttpGetDependency(String category, String service, String testsite, boolean partial, long msMin,
					long msMax) {
				super(category, service, partial, msMin, msMax);
				this.testsite = testsite;
			}

			@Override
			public String getUrl() {
				return testsite;
			}

			@Override
			public boolean test() throws Exception {
				final URL url = new URL(testsite);
				final URLConnection conn = url.openConnection();
				conn.connect();
				return true;
			}
		}

//		addDependency(new TestableDependency("database", "sigagcds", false, 0, 10000) {
//
//			@Override
//			public String getUrl() {
//				return getProperty("datasource.name");
//			}
//
//			@Override
//			public boolean test() throws Exception {
//				try (TemisApiV1Context ctx = new TemisApiV1Context()) {
//					ctx.init(null);
//					return CpDao.getInstance().dt() != null;
//				}
//			}
//
//			@Override
//			public boolean isPartial() {
//				return false;
//			}
//		});
		System.out.println("*** TEMIS API INIT COMPLETE");
	}

	private void defineProperties() {
		addPublicProperty("datasource.name", "java:/jboss/datasources/TemisDS");
		addPublicProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
	}

	@Override
	public String getService() {
		return "sigagc";
	}

	@Override
	public String getUser() {
		return ContextoPersistencia.getUserPrincipal();
	}

	@Override
	public void invoke(SwaggerContext context) throws Exception {
		try {
			if (!context.getAction().getClass().isAnnotationPresent(AcessoPublico.class)) {
//				try {
//					ContextoPersistencia.setUserPrincipal(AutenticadorFabrica.getInstance()
//							.obterPrincipal(context.getRequest(), context.getResponse()));
//				} catch (Exception e) {
//					if (!context.getAction().getClass().isAnnotationPresent(AcessoPublicoEPrivado.class))
//						throw e;
//				}
			}
			super.invoke(context);
		} finally {
			ContextoPersistencia.removeUserPrincipal();
		}
	}

	@Override
	public String getProp(String nome) {
		return getProperty(nome);
	}

}
