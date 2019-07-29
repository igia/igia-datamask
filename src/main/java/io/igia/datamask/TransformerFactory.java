/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v.
 * 2.0 with a Healthcare Disclaimer.
 * A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
 * be found under the top level directory, named LICENSE.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * If a copy of the Healthcare Disclaimer was not distributed with this file, You
 * can obtain one at the project website https://github.com/igia.
 *
 * Copyright (C) 2018-2019 Persistent Systems, Inc.
 */
package io.igia.datamask;

import java.util.Map;

import org.apache.log4j.Logger;

public final class TransformerFactory {
	
	static final Logger logger = Logger.getLogger(TransformerFactory.class);

	public enum SupportedTransformer {
		DATE_OFFSET("DATE_OFFSET", DateOffsetTransformer.class), 
		ZIP_CODE("ZIP_CODE", ZipCodeTransformer.class), 
		BIRTHDATE("BIRTHDATE", BirthdateTransformer.class), 
		IDENTIFIER("IDENTIFIER", IdentifierTransformer.class), 
		NAME("NAME", NameTransformer.class), 
		NUMERIC("NUMERIC", NumericTransformer.class), 
		NUMERIC_IDENTIFIER("NUMERIC_IDENTIFIER", NumericIdentifierTransformer.class), 
		TEXT("TEXT", TextTransformer.class),
		EMAIL("EMAIL", EmailTransformer.class),
		IPADDRESS("IPADDRESS", IpAddressTransformer.class),
		TOKENIZE("TOKENIZE", TokenizeTextTransformer.class),
		URL("URL", UrlTransformer.class);

		private final String name;
		private final Class<?> clazz;

		SupportedTransformer(final String name, final Class<? extends Transformer> clazz) {
			this.name = name;
			this.clazz = clazz;
		}

		public String getName() {
			return this.name;
		}

		public Class<?> getClazz() {
			return this.clazz;
		}

		public static SupportedTransformer forName(String name) {
			for (final SupportedTransformer s : values()) {
				if (s.name.equals(name)) {
					return s;
				}
			}
			return null; // not supported
		}
	}
	
	Transformer create(String name, String path, String nodeTypeString, Map<String, String> params) {
		logger.debug("Transformer.create(name: "+name+", path: "+path+", nodeTypeString: "+nodeTypeString+")");
		if (name==null) {
			logger.error("TransformerFactory.create requires class identifier");
			throw new DatamaskException("TransformerFactory.create requires class identifier");
		}
		SupportedTransformer trans = SupportedTransformer.forName(name.toUpperCase());
		if (trans==null) {
			logger.error("TransformerFactory could find factory class: "+name);
			throw new DatamaskException("TransformerFactory could not find factory class: "+name);
		}
		Class<?> clazz = trans.getClazz();
		logger.debug("Transformer.create(): creating Transformer class instance: "+clazz.getName());
		Transformer t = null;
		try {
			t = (Transformer) clazz.newInstance();
		}
		catch (Exception e) {
			logger.error("Failed to instantiate class: "+clazz.getName()+"; "+e.getMessage());
			throw new DatamaskException("Failed to instantiate class: "+clazz.getName());
		}
		logger.debug("Transformer.create(): setting properties: "+clazz.getName());
		t.setPath(path);
		t.setNodeType(nodeTypeString);
		t.setRule(name);
		t.setParams(params);
		return t;
	}
}