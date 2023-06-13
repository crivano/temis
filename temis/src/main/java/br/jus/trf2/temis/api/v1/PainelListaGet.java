package br.jus.trf2.temis.api.v1;

import br.jus.trf2.temis.api.v1.ITemisApiV1.IPainelListaGet;

public class PainelListaGet implements IPainelListaGet {

	@SuppressWarnings("unchecked")
	@Override
	public void run(Request req, Response resp, TemisApiV1Context ctx) throws Exception {
	}

	@Override
	public String getContext() {
		return "obter lista de documentos por marcador";
	}

}
