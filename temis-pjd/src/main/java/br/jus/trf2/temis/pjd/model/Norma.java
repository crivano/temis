package br.jus.trf2.temis.pjd.model;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;

import org.joda.time.LocalDate;

import com.crivano.juia.annotations.Edit;
import com.crivano.juia.annotations.EditKindEnum;
import com.crivano.juia.annotations.Global;
import com.crivano.juia.annotations.Global.Gender;
import com.crivano.juia.annotations.Search;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.Evento;
import br.jus.trf2.temis.core.util.FullSerialization;
import br.jus.trf2.temis.core.util.NoSerialization;
import br.jus.trf2.temis.core.util.Texto;
import br.jus.trf2.temis.pjd.model.enm.DispositivoDeNormaEnum;
import br.jus.trf2.temis.pjd.model.enm.TipoDeNormaEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode
@FieldNameConstants
//@Menu(list = true)
@Global(singular = "Norma Jurídica", plural = "Normas Jurídicas", gender = Gender.SHE, locator = "pro-norma", codePrefix = "NJ", deletable = false)
public class Norma extends Entidade {
	@Entity
	@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
	@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, length = 128)
	@Data
	@EqualsAndHashCode
	@FieldNameConstants
	@Global(singular = "Evento de Norma Jurídica", plural = "Eventos de Normas Jurídicas", gender = Gender.HE, codePrefix = "EN")
	public abstract static class EventoNorma extends Evento<Norma, EventoNorma> {
		@NoSerialization
		@ManyToOne(fetch = FetchType.LAZY)
		private Norma norma;
	}

	@OneToMany(mappedBy = EventoNorma.Fields.norma, cascade = CascadeType.ALL)
	@OrderBy(Evento.Fields.dtIni)
	private SortedSet<EventoNorma> evento = new TreeSet<>();

	@FullSerialization
	@Edit(caption = "Pai", colM = 4, colL = 4)
	@ManyToOne(fetch = FetchType.LAZY)
	private Norma normaPai;

	String identificador;

	@Search
	String referencia;

	@Edit(caption = "Tipo", colM = 4, colL = 4)
	@Enumerated(EnumType.STRING)
	TipoDeNormaEnum tipo;

	@Edit(caption = "Número", colM = 4, colL = 4)
	String numero;

	@Edit(caption = "Data", colM = 4, colL = 4)
	LocalDate data;

	@Search
	@Edit(caption = "Dispositivo", colM = 4, colL = 4)
	@Enumerated(EnumType.STRING)
	DispositivoDeNormaEnum dispositivo;

	@Search
	@Edit(caption = "Codigo", colM = 4, colL = 4)
	String codigo;

	@Search
	@Edit(caption = "Texto", kind = EditKindEnum.TEXTAREA)
	@Column(length = 2048)
	String texto;

	public Norma(Norma normaPai, DispositivoDeNormaEnum tipo, String codigo, String texto) {
		super();
		this.normaPai = normaPai;
		this.dispositivo = tipo;
		this.codigo = codigo;
		this.texto = texto;
	}
	
	@PrePersist
	public void onSave() {
		this.identificador = buildIdentificador();
		this.referencia = buildReferencia();
	}

	private String buildIdentificador() {
		if (normaPai != null && normaPai.getDispositivo() == null)
			normaPai = null;
		if (normaPai == null)
			if (dispositivo == DispositivoDeNormaEnum.NORMA)
				return getIdentificadorCompacta();
			else
				throw new RuntimeException("Norma sem especificação de normaPai só pode ser criada com o tipo 'Norma'");

		if (normaPai.getDispositivo() != DispositivoDeNormaEnum.ARTIGO
				&& normaPai.getDispositivo() != DispositivoDeNormaEnum.INCISO)
			if (normaPai.getDispositivo().ordinal() != dispositivo.ordinal() - 1)
				throw new RuntimeException("Norma do tipo '" + dispositivo.getNome() + "' não pode ser filha de '"
						+ normaPai.getDispositivo().getNome() + "'");

		return normaPai.getIdentificador() + "_" + getIdentificadorCompacta();
	}

	private String buildReferencia() {
		if (normaPai != null && normaPai.getDispositivo() == null)
			normaPai = null;
		if (normaPai == null)
			if (dispositivo == DispositivoDeNormaEnum.NORMA)
				return getCodigo();

		return normaPai.getReferencia() + ", " + getReferenciaCompacta();
	}

	public String getReferenciaCompacta() {
		String s = codigo;
		switch (dispositivo) {
		case PARAGRAFO:
			if (Texto.slugify(s, true, true).contains("unico"))
				return "parágrafo único";
			return "§ " + s + (isNumberLowerThan10(s) ? "º" : "");
		case ARTIGO:
			return "art. " + s + (isNumberLowerThan10(s) ? "º" : "");
		}
		return s;
	}

	public String getIdentificadorCompacta() {
		String s = buildFragmentoDeIdentificador(codigo);
		switch (dispositivo) {
		case PARAGRAFO:
			if (s.contains("paragrafo_unico"))
				return "par_unico";
			return "par_" + s;
		case ARTIGO:
			return "art_" + s;
		}
		return s;
	}

	public static String buildFragmentoDeIdentificador(String s) {
		return Texto.slugify(s, true, true);
	}

	public static boolean isNumberLowerThan10(String s) {
		try {
			int i = Integer.parseInt(s);
			if (i < 10)
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getDescrCompleta() {
		return getReferenciaCompacta() + (getTexto() != null ? ": " + getTexto() : "");
	}

}
