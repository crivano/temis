package br.jus.trf2.temis.iam.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.FieldSet;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.iam.model.enm.AgentePapelEnum;
import lombok.Data;

@Entity
@Data
@Global(singular = "Empresa", plural = "Empresas", gender = Gender.SHE, locator = "iam-empresa")
public class Empresa extends Entidade implements Comparable<Empresa> {

	@Data
	@Global(singular = "Usuário", plural = "Usuários", gender = Gender.HE)
	public static class XAgente {
		@Edit(caption = "Gmail", colM = 6)
		String gmail;

		@Enumerated(EnumType.STRING)
		@Edit(caption = "Papel", colM = 5)
		AgentePapelEnum agentePapel;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Search
	@Edit(caption = "Identificador", colM = 4)
	private String identificador;

	@Search
	@Edit(caption = "Nome", colM = 8)
	private String nome;

	@Edit(caption = "Razão Social", colM = 6)
	private String razaoSocial;

	@Edit(caption = "CPF/CNPJ", colM = 3, attr = { "ui-br-cpfcnpj-mask" })
	private String cpfcnpj;

	@Edit(caption = "Descrição", colM = 12)
	private String descricao;

	@FieldSet(caption = "Usuários")
	@Edit
	List<XAgente> xAgente;

	@FieldSet(caption = "Endereço Principal")
	@Edit()
	Endereco endereco;

	@FieldSet(caption = "Comunicação")
	@Edit(caption = "Email para Resposta", colM = 6)
	private String emailParaResposta;

	@Edit(caption = "Nome para Resposta", colM = 6)
	private String emailParaRespostaNome;

	@Edit(caption = "Email para Cópia Oculta", colM = 6)
	private String emailCopiaOculta;

	@Edit(caption = "Nome para Cópia Oculta", colM = 6)
	private String emailCopiaOcultaNome;

	@FieldSet(caption = "Opções")
	@Edit(caption = "Gerir Documentos", attrContainer = "ng-show=usuario.modules.indexOf('ecm') > -1", colM = 3)
	private boolean podeEcm;

	@Edit(caption = "Gerir Negócio", attrContainer = "ng-show=usuario.modules.indexOf('biz') > -1", colM = 3)
	private boolean podeBiz;

	@Edit(caption = "Gerir Torneios", attrContainer = "ng-show=usuario.modules.indexOf('trn') > -1", colM = 3)
	private boolean podeTrn;

//	@FieldSet(caption = "Loja Virtual", strong = true)
//	@Edit(caption = "Tipo", colM = 4)
//	private TipoDeLojaVirtualEnum tipoDeLojaVirtual;
//
//	@Edit(caption = "URL", colM = 4)
//	private String url;
//
//	@Edit(caption = "Número WhatsApp", colM = 4)
//	private String numeroDeWhatsAppDaLojaVirtual;
//
//	@Edit(caption = "Ícone do Aplicativo (1024x1024px)", kind = EditKindEnum.FILE, attr = { "accept=image/png",
//			"ngf-pattern=image.png", "ngf-max-size=100KB" })
//	private @OneToMany ArquivoPng> refArquivoIcone;

	@Edit(caption = "Cabeçalho", kind = EditKindEnum.TEXTAREA, attr = "rows=10", colM = 6)
	private String cabecalhoDaLojaVirtual;

	@Edit(caption = "Rodapé", kind = EditKindEnum.TEXTAREA, attr = "rows=10", colM = 6)
	private String rodapeDaLojaVirtual;

	@FieldSet(caption = "Configuração do Firebase", strong = true)
	@Edit(caption = "API Key", colM = 4)
	private String firebaseApiKey;

	@Edit(caption = "Auth Domain", colM = 4)
	private String firebaseAuthDomain;

	@Edit(caption = "Database URL", colM = 4)
	private String firebaseDatabaseUrl;

	@Edit(caption = "Project ID", colM = 4)
	private String firebaseProjectId;

	@Edit(caption = "Storage Bucket", colM = 4)
	private String firebaseStorageBucket;

	@Edit(caption = "Messaging Sender ID", colM = 4)
	private String firebaseMessagingSenderId;

	@Edit(caption = "API ID", colM = 4)
	private String firebaseApiId;

//	@AdministratorOnlySerialization
//	@Edit(caption = "Server Key", colM = 12)
//	private String firebaseServerKey;
//
//	@FieldSet(caption = "Configurações Documentais")
//	@Edit
//	EcmConfig ecmConfig;
//
//	@FieldSet(caption = "Configurações de Negócios")
//	@Edit
//	BizConfig bizConfig;

	public String getSelectFirstLine() {
		return identificador;
	}

	public String getSelectSecondLine() {
		return nome;
	}

	@Override
	public String getCode() {
		return this.identificador;
	}

	@Override
	public String getDescr() {
		return this.nome;
	}

	@Override
	public int compareTo(Empresa o) {
		return this.getId().compareTo(o.getId());
	}
}