package br.jus.trf2.temis.cae.module;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import br.jus.trf2.temis.cae.model.CaeAtividade;
import br.jus.trf2.temis.cae.model.CaeAtividade.CaeEventoDeAtividade;
import br.jus.trf2.temis.cae.model.CaeConvolacao;
import br.jus.trf2.temis.cae.model.CaeCurso;
import br.jus.trf2.temis.cae.model.CaeInstituicaoDeEnsino;
import br.jus.trf2.temis.cae.model.CaeTematica;
import br.jus.trf2.temis.cae.model.CaeTipoDeAtividade;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeAprovacao;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeDeferimento;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeIndeferimento;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeInscricao;
import br.jus.trf2.temis.cae.model.event.CaeEventoDeAtividadeReprovacao;
import br.jus.trf2.temis.core.module.TemisApp;
import br.jus.trf2.temis.core.module.TemisModuleSupport;

@ApplicationScoped
public class CaeTemisModule extends TemisModuleSupport {

	@Inject
	private TemisApp app;

	@PostConstruct
	public void onPostConstruct() {
		app.register(CaeInstituicaoDeEnsino.class);
		app.register(CaeTipoDeAtividade.class);
		app.register(CaeAtividade.class);
		app.register(CaeTematica.class);
		app.register(CaeCurso.class);
		app.register(CaeCurso.XTematica.class);
		app.register(CaeCurso.XAtividade.class);
		app.register(CaeConvolacao.class);
		app.register(CaeConvolacao.XTipoDeAtividade.class);

		app.register(CaeEventoDeAtividade.class);
		app.register(CaeEventoDeAtividadeInscricao.class);
		app.register(CaeEventoDeAtividadeDeferimento.class);
		app.register(CaeEventoDeAtividadeIndeferimento.class);
		app.register(CaeEventoDeAtividadeAprovacao.class);
		app.register(CaeEventoDeAtividadeReprovacao.class);

		app.addModule("cae", this);
		System.out.println("*** CAE INIT");
	}

	public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
	}

}
