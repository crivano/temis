package br.jus.trf2.temis.core.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import br.jus.trf2.temis.core.util.Prop;
import br.jus.trf2.temis.core.util.SigaEntityManagerFactory;

//@NoArgsConstructor
public class SigaEntityManagerFactoryImpl implements SigaEntityManagerFactory {

	private boolean initialized = false;

	public SigaEntityManagerFactoryImpl() {
		System.out.println("*** SIGA JPA EMF INIT COMPLETE");
	}

	@PostConstruct
	private void postConstruct() {
//		addProperties();
//		addClasses();
	}

	private final List<Class> entityClasses = new ArrayList<>();
	Properties properties = new Properties();

	@Override
	public void addClass(Class clazz) {
		entityClasses.add(clazz);
	}

	@Override
	public void addProperty(String key, Object value) {
		properties.put(key, value);
	}

	@Override
	public EntityManager getEntityManager() {
		return getEntityManagerFactory().createEntityManager();
	}

	protected EntityManagerFactory getEntityManagerFactory() {
		if (!initialized) {
			initialized = true;
			addProperties();
			addClasses();
			System.out.println("*** SIGA JPA EMF INITIALIZED");
		}
		PersistenceUnitInfo persistenceUnitInfo = getPersistenceUnitInfo(getClass().getSimpleName());
		Map<String, Object> configuration = new HashMap<>();
		EntityManagerFactoryBuilderImpl emfb = new EntityManagerFactoryBuilderImpl(
				new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration);
		EntityManagerFactory emf = emfb.build();

//		Metadata metadata = emfb.getMetadata();
//		SchemaExport schemaExport = new SchemaExport();
//		schemaExport.setFormat(true);
//		schemaExport.setOutputFile("c:/tmp/create.sql");
//		schemaExport.createOnly(EnumSet.of(TargetType.SCRIPT), metadata);

		return emf;
	}

	protected SigaPersistenceUnitInfo getPersistenceUnitInfo(String name) {
		return new SigaPersistenceUnitInfo(name, getEntityClassNames(), getProperties());
	}

	protected List<String> getEntityClassNames() {
		return entityClasses.stream().map(Class::getName).collect(Collectors.toList());
	}

	protected Properties getProperties() {
		return properties;
	}

	@Override
	public void addCache(String name, String strategy, int wakeUpInterval, int maxEntries, int lifespan, int maxIdle) {
		addProperty("hibernate.cache.infinispan." + name + ".eviction.strategy", strategy);
		addProperty("hibernate.cache.infinispan." + name + ".eviction.wake_up_interval",
				Integer.toString(wakeUpInterval));
		addProperty("hibernate.cache.infinispan." + name + ".eviction.max_entries", Integer.toString(maxEntries));
		addProperty("hibernate.cache.infinispan." + name + ".expiration.lifespan", Integer.toString(lifespan));
		addProperty("hibernate.cache.infinispan." + name + ".expiration.max_idle", Integer.toString(maxIdle));
	}

	@Override
	public void addProperties() {
//      properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//      properties.put("hibernate.id.new_generator_mappings", false);
//		Prop.setProvider(pp);
		addProperty("hibernate.connection.datasource", Prop.get("datasource.name"));

		addProperty("hibernate.cache.use_query_cache", "true");
		addProperty("hibernate.cache.use_second_level_cache", "true");
		addProperty("hibernate.current_session_context_class", "thread");
		addProperty("hibernate.query.substitutions", "true 1, false 0");

		// Desabilita o novo gerador de IDs para manter a compatibilidade
		addProperty("hibernate.id.new_generator_mappings", "false");
		addProperty("hibernate.max_fetch_depth", "3");
		addProperty("hibernate.default_batch_fetch_size", "1000");
		addProperty("hibernate.dialect", Prop.get("hibernate.dialect"));
		addProperty("hibernate.hbm2ddl.auto", "none");
		addProperty("hibernate.show_sql", "false");
		addProperty("hibernate.enable_lazy_load_no_trans", "true");

		// Cache Region QUERY_CACHE_SECONDS
		addCache("querySeconds", "LRU", 2500, 10000, 5000, 5000);

		// Cache Region CACHE_QUERY_HOURS
		addCache("queryHours", "LRU", 2500, 10000, 36000000, 3600000);

		// Cache Region CACHE_QUERY_SUBSTITUICAO
		addCache("querySubstituicao", "LRU", 2500, 10000, 10000, 10000);

		// Cache Region CACHE_HOURS
		addCache("hours", "LRU", 2500, 10000, 36000000, 3600000);

		// Cache Region CACHE_SECONDS
		addCache("seconds", "LRU", 2500, 10000, 5000, 5000);

		// Cache Region CACHE_CORPORATIVO
		addCache("corporativo", "LRU", 2500, 10000, 300000, 300000);

		// Cache Region CACHE_QUERY_CORPORATIVO 10 minutos
		addCache("queryCorporativo", "LRU", 2500, 10000, 600000, 300000);
	}

	@Override
	public void addClasses() {
//		addClass(DpCargo.class);
//		addClass(DpFuncaoConfianca.class);
//		addClass(DpLotacao.class);
//		addClass(DpPessoa.class);
//		addClass(CpOrgao.class);
//		addClass(CpOrgaoUsuario.class);
//		addClass(DpSubstituicao.class);
//		addClass(CpFeriado.class);
//		addClass(CpOcorrenciaFeriado.class);
//		addClass(CpAplicacaoFeriado.class);
//		addClass(CpLocalidade.class);
//		addClass(CpUF.class);
//		addClass(CpPersonalizacao.class);
//		addClass(CpTipoPessoa.class);
//		addClass(CpConfiguracao.class);
//		addClass(CpConfiguracaoCache.class);
////        addClass(CpSituacaoConfiguracao.class);
////        addClass(CpTipoConfiguracao.class);
//		addClass(CpServico.class);
//		addClass(CpTipoGrupo.class);
//		addClass(CpGrupo.class);
//		addClass(CpGrupoDeEmail.class);
//		addClass(CpPerfil.class);
//		addClass(CpPerfilJEE.class);
//		addClass(CpTipoLotacao.class);
//		addClass(CpTipoPapel.class);
//		addClass(CpPapel.class);
//		addClass(CpTipoServico.class);
//		addClass(CpTipoIdentidade.class);
//		addClass(CpIdentidade.class);
//		addClass(CpModelo.class);
//		addClass(CpUnidadeMedida.class);
//		addClass(CpComplexo.class);
//		addClass(CpMarcador.class);
//		addClass(CpMarca.class);
//		addClass(CpTipoMarca.class);
//		addClass(CpAcesso.class);
//		addClass(DpVisualizacao.class);
//		addClass(DpVisualizacaoAcesso.class);
//		addClass(CpContrato.class);
//		addClass(CpToken.class);
//
//		addClass(CpArquivo.class);
//		addClass(CpArquivoBlob.class);
//		addClass(CpArquivoExcluir.class);
//
//		addClass(CpMarcadorCorEnum.class);
//		addClass(CpMarcadorIconeEnum.class);
//		addClass(CpMarcadorTipoAplicacaoEnum.class);
//		addClass(CpMarcadorTipoDataEnum.class);
//		addClass(CpMarcadorTipoExibicaoEnum.class);
//		addClass(CpMarcadorTipoInteressadoEnum.class);
//		addClass(CpMarcadorTipoTextoEnum.class);
//		addClass(CpTipoMarcadorEnum.class);
//		addClass(CpSituacaoDeConfiguracaoEnum.class);
//
//		addClass(CpMarcadorCorEnumConverter.class);
//		addClass(CpMarcadorIconeEnumConverter.class);
//		addClass(CpMarcadorFinalidadeEnumConverter.class);
//		addClass(CpMarcadorTipoAplicacaoEnumConverter.class);
//		addClass(CpMarcadorTipoDataEnumConverter.class);
//		addClass(CpMarcadorTipoExibicaoEnumConverter.class);
//		addClass(CpMarcadorTipoInteressadoEnumConverter.class);
//		addClass(CpMarcadorTipoTextoEnumConverter.class);
//		addClass(CpMarcadorGrupoEnumConverter.class);
//		addClass(CpTipoMarcadorEnumConverter.class);
//		addClass(CpSituacaoDeConfiguracaoEnumConverter.class);

	}
}