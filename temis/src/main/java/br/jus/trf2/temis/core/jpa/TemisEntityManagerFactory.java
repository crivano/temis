package br.jus.trf2.temis.core.jpa;

import javax.enterprise.inject.Specializes;
import javax.inject.Singleton;

import br.jus.trf2.temis.core.Arquivo;
import br.jus.trf2.temis.core.ArquivoAssinaturaDigital;
import br.jus.trf2.temis.core.ArquivoHtml;
import br.jus.trf2.temis.core.ArquivoImagem;
import br.jus.trf2.temis.core.ArquivoJpeg;
import br.jus.trf2.temis.core.ArquivoJson;
import br.jus.trf2.temis.core.ArquivoPdf;
import br.jus.trf2.temis.core.ArquivoPng;
import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.util.CustomImplicitNamingStrategy;
import br.jus.trf2.temis.core.util.CustomPhysicalNamingStrategy;
import br.jus.trf2.temis.crp.model.CrpLotacao;
import br.jus.trf2.temis.crp.model.CrpPessoa;
import br.jus.trf2.temis.iam.model.Agente;
import br.jus.trf2.temis.iam.model.Endereco;
import br.jus.trf2.temis.iam.model.Pessoa;
import br.jus.trf2.temis.iam.model.Unidade;

@Singleton
@Specializes
//@NoArgsConstructor
//@Default
public class TemisEntityManagerFactory extends SigaEntityManagerFactoryImpl {

	public TemisEntityManagerFactory() {
		super();
		System.out.println("*** TEMIS JPA EMF CREATED");
	}

	public void addProperties() {
		super.addProperties();

		addProperty("hibernate.hbm2ddl.auto", "update");
		addProperty("hibernate.physical_naming_strategy", CustomPhysicalNamingStrategy.class.getName());
		addProperty("hibernate.implicit_naming_strategy", CustomImplicitNamingStrategy.class.getName());
		addProperty("jadira.usertype.autoRegisterUserTypes", "true");
		addProperty("jadira.usertype.javaZone", "UTC");
		addProperty("jadira.usertype.currencyCode", "BRL");

		// Cache Region CACHE_EX
		addCache("ex", "LRU", 2500, 10000, 300000, 300000);

		System.out.println("*** TEMIS JPA EMF INIT COMPLETE");
	}

	public void addClasses() {
		super.addClasses();

		addClass(Unidade.class);
		addClass(Pessoa.class);
		addClass(Endereco.class);

		addClass(Arquivo.class);
		addClass(ArquivoAssinaturaDigital.class);
		addClass(ArquivoHtml.class);
		addClass(ArquivoImagem.class);
		addClass(ArquivoJpeg.class);
		addClass(ArquivoJson.class);
		addClass(ArquivoPdf.class);
		addClass(ArquivoPng.class);
		addClass(Evento.class);
		addClass(Agente.class);

		addClass(Entidade.class);
		addClass(Etiqueta.class);

		addClass(CrpPessoa.class);
		addClass(CrpLotacao.class);
		System.out.println("*** CRP_PESSOA ADDED");

	}
}
