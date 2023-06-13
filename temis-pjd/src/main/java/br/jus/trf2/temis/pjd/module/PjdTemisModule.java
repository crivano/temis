package br.jus.trf2.temis.pjd.module;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import br.jus.trf2.temis.core.module.TemisApp;
import br.jus.trf2.temis.core.module.TemisModuleSupport;
import br.jus.trf2.temis.pjd.model.Jurisprudencia;
import br.jus.trf2.temis.pjd.model.Norma;
import br.jus.trf2.temis.pjd.model.PeticaoInicial;
import br.jus.trf2.temis.pjd.model.Processo;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualAnotacao;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualAutuacao;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualDefinicaoDeValorDaCausa;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeDocumento;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeDocumentoGenerico;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeFato;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeFontePagadora;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeJurisprudencia;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeNorma;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDeParte;
import br.jus.trf2.temis.pjd.model.event.EventoProcessualInclusaoDePedido;
import br.jus.trf2.temis.pjd.model.event.PeticaoInicialEventoAnotacao;

@ApplicationScoped
public class PjdTemisModule extends TemisModuleSupport {
	@Inject
	private TemisApp app;

	@PostConstruct
	public void onPostConstruct() {
		app.register(PeticaoInicial.class);
		app.register(PeticaoInicial.XPessoa.class);
		app.register(PeticaoInicial.XFato.class);
		app.register(PeticaoInicial.XDano.class);
		app.register(PeticaoInicial.XDireito.class);
		app.register(PeticaoInicial.XJurisprudencia.class);
		app.register(PeticaoInicial.XPedido.class);
		app.register(PeticaoInicial.XProva.class);
		app.register(PeticaoInicial.XEvento.class);
		app.register(PeticaoInicialEventoAnotacao.class);

		app.register(Processo.class);
		app.register(Processo.EventoProcessual.class);
		app.register(EventoProcessualAnotacao.class);
		app.register(EventoProcessualInclusaoDeParte.class);
		app.register(EventoProcessualInclusaoDeFato.class);
		app.register(EventoProcessualInclusaoDeNorma.class);
		app.register(EventoProcessualInclusaoDeDocumento.class);
		app.register(EventoProcessualInclusaoDeDocumentoGenerico.class);
		app.register(EventoProcessualInclusaoDeDocumentoComprobatorioDeEpp.class);
		app.register(EventoProcessualInclusaoDeFontePagadora.class);
		app.register(EventoProcessualInclusaoDeJurisprudencia.class);
		app.register(EventoProcessualInclusaoDePedido.class);
		app.register(EventoProcessualDefinicaoDeValorDaCausa.class);
		app.register(EventoProcessualAutuacao.class);

		app.register(Norma.class);
		app.register(Norma.EventoNorma.class);

		app.register(Jurisprudencia.class);
		app.register(Jurisprudencia.EventoJurisprudencia.class);

		app.addModule("pjd", this);
		System.out.println("*** PJD INIT");
	}

	public void onStart(@Observes @Initialized(ApplicationScoped.class) Object pointless) {
	}

}
