package br.jus.trf2.temis.core.module;

import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletContext;

import org.jboss.logging.Logger;

import br.jus.trf2.temis.core.util.AppResources;
import br.jus.trf2.temis.crp.model.CrpPessoa;

@ApplicationScoped
public class TemisModule extends TemisModuleSupport {

	private final static org.jboss.logging.Logger log = Logger.getLogger(TemisModule.class);
	public static EntityManagerFactory emf;

	@Inject
	public void setEM(EntityManagerFactory factory) {
		emf = factory;
	}

	@Inject
	ServletContext context;

	@Inject
	AppResources app;

	@Inject
	private TemisApp tapp;

	public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
	}

	@PostConstruct
	public void init() {
		System.out.println("*** CORE INIT");

//		app.addResource(KindEnum.RESOURCE_CSS, "core/javascripts/angular-busy/angular-busy.css");
//		app.addResource(KindEnum.RESOURCE_CSS, "core/javascripts/angucomplete-alt/angucomplete-alt.css");
//		app.addResource(KindEnum.RESOURCE_CSS, "core/javascripts/angular-datepicker/angular-datepicker.min.css");
//		app.addResource(KindEnum.RESOURCE_CSS, "core/app.css");
//		app.addResource(KindEnum.RESOURCE_CSS, "core/javascripts/pivottable/pivot.css");
//		app.addResource(KindEnum.RESOURCE_CSS, "core/javascripts/printjs/print.min.css");
//
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/angular-input-masks/angular-input-masks-standalone.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/ngmask/ngmask.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/angular-busy/angular-busy.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/webcam-directive/webcam.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/angucomplete-alt/angucomplete-alt.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/angular-modal-service/angular-modal-service.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/dir-pagination/dirPagination.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/angularjs-dropdown-multiselect.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/angular-datepicker/angular-datepicker.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/ng-file-upload.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/html-parser/parser.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/marked/textoz-marked.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/javascripts/printjs/print.min.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/util.js");
//		app.addResource(KindEnum.RESOURCE_JS, "core/app.js");

		app.addModule("core", new IResources() {
			@Override
			public InputStream getResourceAsStream(String filename) {
				InputStream r = context.getResourceAsStream(filename);
				if (r == null)
					throw new RuntimeException("resource não encontrado: " + filename);
				return r;
			}
		});
	}

//    @PostConstruct
//    public void init() {
//        log.info("INICIANDO SIGAGC.WAR");
//        CpTipoDeConfiguracao.mapear(CpTipoDeConfiguracao.values());

//        new MigrationThread().start();
//    }

//    public static class MigrationThread extends Thread {
//        public void run() {
//            try {
//                SigaFlyway.migrate("java:/jboss/datasources/SigaGcDS", "classpath:db/mysql/sigagc", true);
//            } catch (NamingException e) {
//                log.error("Erro na migração do banco", e);
//                SigaFlyway.stopJBoss();
//            }
//        }
//    }

}