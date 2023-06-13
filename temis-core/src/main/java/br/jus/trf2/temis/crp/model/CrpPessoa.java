/*******************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 * 
 *     This file is part of SIGA.
 * 
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Criado em  21/12/2005
 *
 */
package br.jus.trf2.temis.crp.model;

import java.util.Date;
import java.util.SortedSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.crivano.jbiz.IEvent;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Etiqueta;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.IEntidade;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Table(name = "corporativo.dp_pessoa")
@Entity
// @SqlResultSetMapping(name = "scalar", columns = @ColumnResult(name = "dt"))
//@Cache(region = CpDao.CACHE_CORPORATIVO, usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Global(singular = "Pessoa", plural = "Pessoas", gender = Gender.SHE, locator = "crp-pessoa", versionable = true)
@Data
@FieldNameConstants
public class CrpPessoa implements IEntidade {

	@Id
	@SequenceGenerator(name = "DP_PESSOA_SEQ", sequenceName = "CORPORATIVO.DP_PESSOA_SEQ")
	@GeneratedValue(generator = "DP_PESSOA_SEQ")
	@Column(name = "ID_PESSOA", unique = true, nullable = false)
//	@Desconsiderar
	private Long id;

	@Column(name = "ID_PESSOA_INICIAL")
//	@Desconsiderar
	private Long idIni;

	@Column(name = "DATA_FIM_PESSOA", length = 19)
	@Temporal(TemporalType.TIMESTAMP)
	private Date termino;

	@Column(name = "DATA_INI_PESSOA", length = 19)
	@Temporal(TemporalType.TIMESTAMP)
//	@Desconsiderar
	private Date inicio;

	@Column(name = "IDE_PESSOA", length = 256)
//	@Desconsiderar
	private String ide;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_NASC_PESSOA")
	private Date dataNascimento;

	@Search(caption = "Nome")
	@Column(name = "NOME_PESSOA", nullable = false, length = 60)
	private String nome;

	/*
	 * Alteracao cartao 1041
	 */
	@Column(name = "NOME_PESSOA_AI", length = 60)
//	@Desconsiderar
	private java.lang.String nomeAI;
	/*
	 * Final Alteracao 1041
	 */

	@Search(caption = "CPF")
	@Column(name = "CPF_PESSOA", nullable = false)
	private Long cpf;

	@Column(name = "MATRICULA")
	private Long matricula;

	@Column(name = "SESB_PESSOA", length = 10)
	private String sesb;

	@Column(name = "EMAIL_PESSOA", length = 60)
	private String email;

	@Column(name = "SIGLA_PESSOA", length = 10)
	private String sigla;

	@Column(name = "DSC_PADRAO_REFERENCIA_PESSOA", length = 16)
	private String padraoReferencia;

	@Column(name = "SITUACAO_FUNCIONAL_PESSOA", length = 50)
	private String situacaoFuncional;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_INICIO_EXERCICIO_PESSOA")
	private Date dataExercicio;

	@Column(name = "ATO_NOMEACAO_PESSOA", length = 50)
	private String atoNomeacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_NOMEACAO_PESSOA")
	private Date dataNomeacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_POSSE_PESSOA")
	private Date dataPosse;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATA_PUBLICACAO_PESSOA")
	private Date dataPublicacao;

	@Column(name = "GRAU_INSTRUCAO_PESSOA", length = 50)
	private String grauInstrucao;

	@Column(name = "ID_PROVIMENTO")
	private Integer idProvimento;

	@Column(name = "NACIONALIDADE_PESSOA", length = 60)
	private String nacionalidade;

	@Column(name = "NATURALIDADE_PESSOA", length = 60)
	private String naturalidade;

	@Column(name = "FG_IMPRIME_END", length = 1)
	private String imprimeEndereco;

	@Column(name = "SEXO_PESSOA", length = 1)
	private String sexo;

	@Column(name = "TP_SERVIDOR_PESSOA")
	private Integer tipoServidor;

	@Column(name = "TP_SANGUINEO_PESSOA", length = 3)
	private String tipoSanguineo;

	@Column(name = "ENDERECO_PESSOA", length = 100)
	private String endereco;

	@Column(name = "BAIRRO_PESSOA", length = 50)
	private String bairro;

	@Column(name = "CIDADE_PESSOA", length = 30)
	private String cidade;

	@Column(name = "CEP_PESSOA", length = 8)
	private String cep;

	@Column(name = "TELEFONE_PESSOA", length = 30)
	private String telefone;

	@Column(name = "RG_PESSOA", length = 20)
	private String identidade;

	@Column(name = "RG_ORGAO_PESSOA", length = 50)
	private String orgaoIdentidade;

	@Temporal(TemporalType.DATE)
	@Column(name = "RG_DATA_EXPEDICAO_PESSOA")
	private Date dataExpedicaoIdentidade;

	@Column(name = "RG_UF_PESSOA", length = 2)
	private String ufIdentidade;

	@Column(name = "ID_ESTADO_CIVIL")
	private Integer idEstadoCivil;

	@Column(name = "NOME_EXIBICAO")
	private String nomeExibicao;

//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ID_PESSOA_INICIAL", insertable = false, updatable = false)
//	@Desconsiderar
//	private DpPessoa pessoaInicial;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ID_LOTACAO")
//	private DpLotacao lotacao;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ID_CARGO")
//	private DpCargo cargo;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ID_FUNCAO_CONFIANCA")
//	private DpFuncaoConfianca funcaoConfianca;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ID_ORGAO_USU")
//	@Desconsiderar
//	private CpOrgaoUsuario orgaoUsuario;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "ID_TP_PESSOA")
//	private CpTipoPessoa cpTipo;
//
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "pessoaInicial")
//	@OrderBy("idPessoa DESC")
//	@Desconsiderar
//	// private Set<DpPessoa> pessoasPosteriores = new HashSet<DpPessoa>(0);
//	private Set<DpPessoa> pessoasPosteriores;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "HIS_IDC_INI")
//	@Desconsiderar
//	private CpIdentidade hisIdcIni;
//
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "HIS_IDC_FIM")
//	@Desconsiderar
//	private CpIdentidade hisIdcFim;

	// @Override
	public String getFirstLine() {
		return sesb + matricula;
	}

	@Override
	public Date getBegin() {
		return inicio;
	}

	@Override
	public void setBegin(Date dt) {
	}

	@Override
	public SortedSet<? extends IEvent<?>> getEvent() {
		return null;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getCode() {
		return sesb + matricula;
	}

	@Override
	public String getDescr() {
		return nome;
	}

	@Override
	public String getSelectFirstLine() {
		return getCode();
	}

	@Override
	public SortedSet<Etiqueta> getEtiqueta() {
		return null;
	}

	@Override
	public void prePersistAndUpdate() throws Exception {
	}

	@Override
	public SortedSet<? extends Evento<?, ?>> getEvento() {
		return null;
	}

	@Override
	public String getDescrCompleta() {
		return sesb + matricula + " - " + nome;
	}

}
