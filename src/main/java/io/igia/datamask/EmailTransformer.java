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

import com.mifmif.common.regex.Generex;

/**
 * Generate random email.
 *
 */
public class EmailTransformer extends Transformer {
	private static final String REGEX_STR = "[a-z]{2,6}[0-9-_]{1,3}[a-z]{3,7}[@]{1}[a-zA-Z0-9]{3,10}[.]{1}(com|net|org|edu|gov|uk|ca|mil|co|biz|info|name|tech|io|cn|au|in|fr)";
	private Generex generex; 

	EmailTransformer() {
		super();
		generex = new Generex(REGEX_STR);
	}
	
	EmailTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) {
		super(xpath, xpathTypeString, rule, params);
		generex = new Generex(REGEX_STR);
	}

	@Override
	public String mask(String value) {
		return generex.random();
	}
}
