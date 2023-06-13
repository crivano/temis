package br.jus.trf2.temis.core.module;

import java.io.InputStream;

public interface IResources {
	InputStream getResourceAsStream(String filename);
}