package br.jus.trf2.temis.core.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.ocpsoft.prettytime.PrettyTime;

import com.crivano.juia.annotations.Global;

import br.jus.trf2.temis.core.Entidade;
import br.jus.trf2.temis.core.enm.AmbienteEnum;

public class Utils {
	public static String removeAcento(String acentuado) {
		if (acentuado == null)
			return null;
		String temp = new String(acentuado);
		temp = temp.replaceAll("[ÃÂÁÀ]", "A");
		temp = temp.replaceAll("[ÉÈÊ]", "E");
		temp = temp.replaceAll("[ÍÌÎ]", "I");
		temp = temp.replaceAll("[ÕÔÓÒ]", "O");
		temp = temp.replaceAll("[ÛÚÙÜ]", "U");
		temp = temp.replaceAll("[Ç]", "C");
		temp = temp.replaceAll("[ãâáà]", "a");
		temp = temp.replaceAll("[éèê]", "e");
		temp = temp.replaceAll("[íìî]", "i");
		temp = temp.replaceAll("[õôóò]", "o");
		temp = temp.replaceAll("[ûúùü]", "u");
		temp = temp.replaceAll("[ç]", "c");
		return temp;
	}

	public static String maiusculasEMinusculas(String s) {
		if (s == null)
			return null;

		final StringBuilder sb = new StringBuilder(s.length());
		boolean f = true;

		for (short i = 0; i < s.length(); i++) {
			String ch = s.substring(i, i + 1);
			if (!ch.toUpperCase().equals(ch.toLowerCase())) {
				if (f) {
					sb.append(ch.toUpperCase());
					f = false;
				} else
					sb.append(ch.toLowerCase());
			} else {
				sb.append(ch);
				f = true;
			}
		}

		s = sb.toString();

		s = s.replace(" E ", " e ");
		s = s.replace(" Da ", " da ");
		s = s.replace(" Das ", " das ");
		s = s.replace(" De ", " de ");
		s = s.replace(" Do ", " do ");
		s = s.replace(" Dos ", " dos ");

		return s;
	}

	public static String urlencode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void copyAllNotNullFields(T to, T from) {
		Class<T> clazz = (Class<T>) from.getClass();
		// OR:
		// Class<T> clazz = (Class<T>) to.getClass();
		List<Field> fields = getAllModelFields(clazz);

		if (fields != null) {
			for (Field field : fields) {
				try {
					field.setAccessible(true);
					Object fromValue = field.get(from);
					if (fromValue != null)
						field.set(to, fromValue);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static List<Field> getAllModelFields(Class aClass) {
		List<Field> fields = new ArrayList<>();
		do {
			Collections.addAll(fields, aClass.getDeclaredFields());
			aClass = aClass.getSuperclass();
		} while (aClass != null);
		return fields;
	}

	public static String calcularTempoRelativo(Date anterior) {
		PrettyTime p = new PrettyTime(Utils.newDate(), new Locale("pt"));

		String tempo = p.format(anterior);
		tempo = tempo.replace(" atrás", "");
		tempo = tempo.replace(" dias", " dias");
		tempo = tempo.replace(" horas", "h");
		tempo = tempo.replace(" minutos", "min");
		tempo = tempo.replace(" segundos", "s");
		tempo = tempo.replace("agora há pouco", "agora");
		return tempo;
	}

	public static Date newDate() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
	}

	public static LocalDateTime newLocalDateTimeUTC() {
		DateTimeZone zone = DateTimeZone.forID("UTC");
		return new LocalDateTime(zone);
	}

	public static String formatExplanation(String explanation) {
		return explanation.replace("_not_", "não").replace("_and_", "e").replace("_or_", "ou");
	}

	public static String sorn(String s) {
		if (s == null || s.length() == 0)
			return null;
		return s;
	}

	public static String sore(String s) {
		if (s == null || s.length() == 0)
			return "";
		return s;
	}

	public static int compare(Object one, Object other) {
		if (one == null && other == null) {
			return 0;
		} else if (one == null && other != null) {
			return 1;
		} else if (one != null && other == null) {
			return -1;
		}
		int result = ((Comparable) one).compareTo(other);
		return result;
	}

	public static String convertStreamToString(java.io.InputStream is) {
		@SuppressWarnings("resource")
		java.util.Scanner s = new java.util.Scanner(is, "UTF-8").useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	public static byte[] convertStreamToByteArray(InputStream stream, long size) throws IOException {

		// check to ensure that file size is not larger than Integer.MAX_VALUE.
		if (size > Integer.MAX_VALUE) {
			return new byte[0];
		}

		byte[] buffer = new byte[(int) size];
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		int line = 0;
		// read bytes from stream, and store them in buffer
		while ((line = stream.read(buffer)) != -1) {
			// Writes bytes from byte array (buffer) into output stream.
			os.write(buffer, 0, line);
		}
		stream.close();
		os.flush();
		os.close();
		return os.toByteArray();
	}

	public static LocalDate getLocalDate() {
		DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
		return new LocalDate(zone);
	}

	public static Date localDateToDateMidnight(LocalDate ld) {
		if (ld == null)
			return null;
		return ld.toDateMidnight(DateTimeZone.forID("America/Sao_Paulo")).toDate();
	}

	public static LocalDate dateMidnightToLocalDate(Date dt) {
		if (dt == null)
			return null;
		return new LocalDate(dt.getTime(), DateTimeZone.forID("America/Sao_Paulo"));
	}

	public static String formatDDMMYYYY(Date dt) {
		if (dt == null)
			return null;
		LocalDateTime ldt = new LocalDateTime(dt.getTime(), DateTimeZone.forID("America/Sao_Paulo"));
		return ldt.toString("dd/MM/yyyy");
	}

	public static String formatDDMMYYYY(LocalDateTime dt) {
		if (dt == null)
			return null;
		LocalDateTime ldt = new LocalDateTime(dt.toDate(), DateTimeZone.forID("America/Sao_Paulo"));
		return ldt.toString("dd/MM/yyyy");
	}

//	public static boolean isValidEmailAddress(String email) {
//		boolean result = true;
//		try {
//			InternetAddress emailAddr = new InternetAddress(email);
//			emailAddr.validate();
//		} catch (AddressException ex) {
//			result = false;
//		}
//		return result;
//	}

	public static String concatenar(List<String> l) {
		StringBuilder sb = new StringBuilder();
		if (l == null || l.size() == 0)
			return null;
		for (int i = 0; i < l.size(); i++) {
			if (i > 0) {
				if (i == l.size() - 1)
					sb.append(" e ");
				else
					sb.append(", ");
			}
			sb.append(l.get(i));
		}
		return sb.toString();
	}

//	public static String getBaseUrl() {
//		ApiProxy.Environment proxyEnvironment = ApiProxy.getCurrentEnvironment();
//		Map<String, Object> attributes = proxyEnvironment.getAttributes();
//		String hostname = (String) attributes.get("com.google.appengine.runtime.default_version_hostname");
//		hostname = hostname.replace("xrphomolo.appspot.com", "hml.xrp.com.br");
//		hostname = hostname.replace("xrpcombr.appspot.com", "xrp.com.br");
//		return ("localhost:9999".equals(hostname) ? "http://" : "https://") + hostname;
//	}
//
//	public static String processMarkdown(String markdown) {
//		if (markdown == null)
//			return null;
//		String result = Processor.process(markdown);
//		return result;
//	}

	public static AmbienteEnum getAmbiente() {
		return AmbienteEnum.DESENVOLVIMENTO;
	}

	public static String concat(String... strings) {
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			if (s == null || "".equals(s))
				continue;
			if (sb.length() > 0)
				sb.append(" ");
			sb.append(s);
		}
		if (sb.length() == 0)
			return null;
		return sb.toString();
	}

	public static String snakeCase(String s) {
		final String regex = "([a-z])([A-Z])";
		final String replacement = "$1_$2";
		return s.replaceAll(regex, replacement).toLowerCase();
	}

	public static boolean empty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static String buildLink(Entidade entidade, String link) {
		return "<a href=\"#/" + entidade.getClass().getAnnotation(Global.class).locator() + "/show/" + entidade.getId()
				+ "\">" + (link == null ? entidade.getCode() : link) + "</a>";
	}

}
