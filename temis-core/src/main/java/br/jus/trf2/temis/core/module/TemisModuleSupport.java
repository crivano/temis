package br.jus.trf2.temis.core.module;

import java.io.InputStream;

public class TemisModuleSupport implements IResources {

	@Override
	public InputStream getResourceAsStream(String filename) {
		return this.getClass().getResourceAsStream(filename);
	}

}
