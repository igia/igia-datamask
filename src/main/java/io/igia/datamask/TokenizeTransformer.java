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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Tokenizes the input text, then replaces each token with any previously
 * masked value, otherwise leaves the text as is
 *
 */
public abstract class TokenizeTransformer extends Transformer {

	static final Logger log = Logger.getLogger(TokenizeTransformer.class);

	TokenizeTransformer() {
		super();
	}

	TokenizeTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) {
		super(xpath, xpathTypeString, rule, params);
	}
	
	protected abstract Pattern getTokenizer();
	
	protected abstract Set<String> getIgnoreTokens();

	@Override
	public String mask(String value) {

		if (value == null || value.length() == 0)
			return "";

		Matcher matcher = getTokenizer().matcher(value);
		List<String> matchList = new ArrayList<>();
		log.trace("Tokenize: '"+value+"'");
		while (matcher.find()) {
			String s = matcher.group(0);
			matchList.add(s); // add match to the list
			log.trace(" : '"+s+"'");
		}
		log.trace("End Tokenize");

		StringBuilder result = new StringBuilder();
		for (String s : matchList) {
			if (getIgnoreTokens().contains(s)) {
				result.append(s);
			} else {
				String lookup = datamaskCache.get(s);
				if (lookup==null) {
					result.append(s);
				} else {
					result.append(lookup);
				}
			}
		}
		return result.toString();
	}
}
