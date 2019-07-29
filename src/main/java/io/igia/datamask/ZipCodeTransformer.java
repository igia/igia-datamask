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

/**
 * Official guidance is that initial three digits of ZIP codes may be included in de-identified information 
 * except when the first three-digits identify ZIP codes with a combined population of 20,000 or fewer persons.
 * In those cases, the first three digits must be listed as "000".
 */
public class ZipCodeTransformer extends Transformer {
	static final Logger logger = Logger.getLogger(ZipCodeTransformer.class);

	ZipCodeTransformer() {
		super();
	}
	
	ZipCodeTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) {
		super(xpath, xpathTypeString, rule, params);
	}

	@Override
	public String mask(String value) {
		return zipCodeMasker(value);
	}
	
	private String zipCodeMasker(String value) {
		switch (value.length()) {
		case 5:
			return "99999";
		case 9:
			return "999999999";
		case 10:
			return "99999-9999";
		default:
			logger.error("Unxpected zip code length; value="+value+"; returning no content");
			return "";
		}
	}
}
