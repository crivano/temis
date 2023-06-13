package br.jus.trf2.temis.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;

import br.jus.trf2.sgbd.Sgbd;

public class CustomImplicitNamingStrategy extends ImplicitNamingStrategyComponentPathImpl {
	@Override
	public Identifier determinePrimaryTableName(ImplicitEntityNameSource source) {
		Identifier id = super.determinePrimaryTableName(source);
		return toIdentifier(Sgbd.calcularNomeDeTabela(id.getText()), source.getBuildingContext());
	}

	@Override
	public Identifier determineJoinColumnName(ImplicitJoinColumnNameSource source) {
		String suffix = null;
		try {
			Field ejb3JoinColumnField = source.getClass().getDeclaredField("this$0");
			ejb3JoinColumnField.setAccessible(true);
			Ejb3JoinColumn ejb3Column = (Ejb3JoinColumn) ejb3JoinColumnField.get(source);

			String className = ejb3Column.getPropertyHolder().getClassName();
			String propertyName = ejb3Column.getPropertyName();
			System.out.println(className + "-" + propertyName);

			if (propertyName.toLowerCase().startsWith(className.toLowerCase())) {
				suffix = propertyName.substring(className.length());
				if (suffix.length() == 0)
					suffix = null;
			} else {
				suffix = propertyName;
			}
			if (suffix != null) {
				suffix = Sgbd.abreviarNomeDaColuna(suffix);
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}

		String name = source.getReferencedColumnName().toString();
		Identifier identifier = toIdentifier(name + (suffix != null ? "_" + suffix : ""), source.getBuildingContext());
		return identifier;
	}

	@Override
	public Identifier determineBasicColumnName(ImplicitBasicColumnNameSource source) {
		try {
			Field ejb3ColumnField = source.getClass().getDeclaredField("this$0");
			ejb3ColumnField.setAccessible(true);
			Ejb3Column ejb3Column = (Ejb3Column) ejb3ColumnField.get(source);

			// explicit naming oder implicit
			String tableName = ejb3Column.getPropertyHolder().getTable().getName();
			String className = ejb3Column.getPropertyHolder().getClassName();
			String mneumonicoDaTabela = Sgbd.abreviarNomeDeTabela(tableName);
			final Identifier basicColumnName = super.determineBasicColumnName(source);

			try {
				Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
				Field field = clazz.getDeclaredField(ejb3Column.getPropertyName());
				field.setAccessible(true);

				Type tipo = field.getType();
				String siglaDaColuna = Sgbd.calcularNomeDaColuna(tipo, basicColumnName.toString());
				String columnName = mneumonicoDaTabela + "_" + siglaDaColuna;
				// System.out.println("> " + tableName + "." + basicColumnName.toString() + " ->
				// " + columnName);
				return Identifier.toIdentifier(columnName);
			} catch (NoSuchFieldException nsfe) {
				// System.out.println("> " + tableName + "." + basicColumnName.toString() + " ->
				// " + mneumonicoDaTabela
				// + "_" + basicColumnName.toString());
				return Identifier.toIdentifier(mneumonicoDaTabela + "_" + basicColumnName.toString());
			}
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
				| ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
