package br.jus.trf2.temis.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.HeaderParam;
import br.com.caelum.vraptor.Options;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.observer.download.Download;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.view.Results;
import br.jus.trf2.temis.core.Arquivo;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.enm.AmbienteEnum;
import br.jus.trf2.temis.core.module.TemisApp;
import br.jus.trf2.temis.core.module.TemisDownload;
import br.jus.trf2.temis.core.module.TemisResourceKindEnum;
import br.jus.trf2.temis.core.util.Dao;
import br.jus.trf2.temis.core.util.Public;
import br.jus.trf2.temis.core.util.Utils;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;
import br.jus.trf2.temis.iam.model.Usuario;
import br.jus.trf2.temis.util.Painel;
import br.jus.trf2.temis.util.Painel.MesaItem;
import br.jus.trf2.temis.util.Painel.TipoDePainelEnum;

@Controller
public class IndexController {
	static Map<String, TemisDownload> map = new HashMap<>();

	@Inject
	TemisApp app;

	@Inject
	private Result result;

	@Inject
	private Dao dao;

	@Inject
	private HttpServletRequest request;

	@Inject
	private HttpServletResponse response;

	@Get
	@Path("/app/usuario")
	public void usuarioLoad(String cont) {
		Usuario usuario = null; // ContextInterceptor.getUsuarioCorrente();
		if (usuario == null) {
			usuario = new Usuario();
			usuario.setLoginUrl("/");
		} else {
			usuario.setLogoutUrl("/#!/sobre");
		}
//		DaoUtils.updateUsuarioCorrente(usuario, false);
//		usuario.updateNonPersistentMembers();
//		ContextInterceptor.setUsuarioCorrente(usuario);
		result.use(Results.json()).from(usuario).recursive().serialize();
	}

	@Public
	@Get
	@Path("/app/resource/{filename*}")
	public Download webAppResource(String filename, @HeaderParam("Accept-Encoding") String encoding,
			@HeaderParam("If-None-Match") String ifNoneMatch) throws Exception {
		TemisDownload r;
		if (!map.containsKey(filename) || Utils.getAmbiente() == AmbienteEnum.DESENVOLVIMENTO)
			map.put(filename, app.getResource(filename));
		r = map.get(filename);

		if (r == null) {
			result.use(Results.status()).notFound();
			;
			return null;
		}
		if (r.etag != null && r.etag.equals(ifNoneMatch)) {
			result.use(Results.status()).notModified();
			return null;
		}
		return r;
	}

	@Public
	@Get
	@Path("/app/resources/{filename}")
	public Download webAppResources(String filename, @HeaderParam("Accept-Encoding") String encoding,
			@HeaderParam("If-None-Match") String ifNoneMatch) throws Exception {
		TemisDownload r;
		if (!map.containsKey(filename) || Utils.getAmbiente() == AmbienteEnum.DESENVOLVIMENTO)
			map.put(filename, app.getResources(TemisResourceKindEnum.getByFilename(filename)));
		r = map.get(filename);

		if (r == null) {
			result.use(Results.status()).notFound();
			;
			return null;
		}
		if (r.etag != null && r.etag.equals(ifNoneMatch)) {
			result.use(Results.status()).notModified();
			return null;
		}
		return r;
	}

	@Get
	@Path("/app/painel")
	public void painel() {
		Pessoa pessoa = null;
		Unidade unidade = null;
		List<Object[]> l = dao.listarDocumentosPorPessoaOuLotacao(pessoa, unidade);

		HashMap<Entidade, List<Etiqueta>> map = new HashMap<>();

		for (Object[] reference : l) {
			Entidade entidade = (Entidade) reference[0];
			Etiqueta etiqueta = (Etiqueta) reference[1];

			if (!map.containsKey(entidade))
				map.put(entidade, new ArrayList<Etiqueta>());

			map.get(entidade).add(etiqueta);
		}

		List<MesaItem> list = Painel.listarReferencias(TipoDePainelEnum.UNIDADE, map, pessoa, unidade,
				dao.consultarDataEHoraDoServidor());
		result.use(Results.json()).from(list).recursive().serialize();
	}

	@Options
	@Path("/app/upload")
	public void upload() {
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Headers", "*");
		result.use(Results.status()).ok();
	}

	@Post
	@Path("/app/upload")
	public void upload(UploadedFile file) {
		try {
			if (file.getFile() == null) {
				throw new RuntimeException("O arquivo a ser anexado não foi selecionado!");
			}
		} catch (IOException e) {
			throw new RuntimeException("Erro obtendo o upload", e);
		}

		Integer numBytes = 0;
		try {
			final byte[] bytes = toByteArray(file);
			if (bytes == null) {
				throw new RuntimeException("Arquivo vazio não pode ser anexado.");
			}
			numBytes = bytes.length;
			if (numBytes > 10 * 1024 * 1024) {
				throw new RuntimeException("Não é permitida a anexação de arquivos com mais de 10MB.");
			}
			Arquivo arq = Arquivo.of(file.getContentType(), file.getFileName(), bytes);
			dao.persist(arq);
			result.use(Results.json()).withoutRoot().from(arq).recursive().serialize();
		} catch (IOException ex) {
			throw new RuntimeException("Falha ao manipular aquivo", ex);
		}
	}

	protected byte[] toByteArray(final UploadedFile upload) throws IOException {
		try (InputStream is = upload.getFile()) {
			// Get the size of the file
			final long tamanho = upload.getSize();

			// Não podemos criar um array usando o tipo long.
			// é necessário usar o tipo int.
			if (tamanho > Integer.MAX_VALUE)
				throw new IOException("Arquivo muito grande");

			// Create the byte array to hold the data
			final byte[] meuByteArray = new byte[(int) tamanho];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < meuByteArray.length
					&& (numRead = is.read(meuByteArray, offset, meuByteArray.length - offset)) >= 0) {
				offset += numRead;
			}

			// Ensure all the bytes have been read in
			if (offset < meuByteArray.length)
				throw new IOException("Não foi possível ler o arquivo completamente " + upload.getFileName());

			return meuByteArray;
		}
	}

	@Get("/app/download/{key}")
	public void download(Long key) throws Exception {
		try {
			Arquivo arq = dao.find(Arquivo.class, key);
			arq.updateHash();

//			if (!(arq instanceof ArquivoPng) && !(arq instanceof ArquivoJpeg))
//				throw new Exception("Apenas imagens podem ser visualizadas dessa forma.");

			this.response.setContentType(arq.getContentType());

			final String ifNoneMatch = request.getHeader("If-None-Match");
			response.setHeader("Cache-Control", "must-revalidate, " + "public");
			response.setHeader("Pragma", "");
			String eTag = Base64.getEncoder().encodeToString(arq.getSha1());
			response.setHeader("ETag", eTag);

			if ((eTag).equals(ifNoneMatch) && ifNoneMatch != null)
				response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			else {
				byte[] bytes = arq.getBytes();
				response.getOutputStream().write(bytes);
				;
			}
			result.use(Results.nothing());
		} catch (Exception ex) {
			response.setStatus(500);
			String message = ex.getMessage();
			if (message == null)
				message = ex.getClass().getName();
			result.use(Results.json()).from(message, "errormsg").recursive().serialize();
		}
	}

}
