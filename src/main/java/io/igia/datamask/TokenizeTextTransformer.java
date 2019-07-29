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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class TokenizeTextTransformer extends TokenizeTransformer {
	// "\t /|;.:!@#$%^&*()-=+_{}[]\\'\"?><`~"
	private final Pattern tokenizer = Pattern.compile("([A-z0-9/-]+|[^A-z0-9/-]+)");
	private final Set<String> ignoreTokens = new HashSet<>(Arrays.asList("table", "tbody", "tr", "td", "th",
			"div", "[", "'", "]", "+", "|", "[", "\\", ".", "!", "?", ";", ":", "-", "/", "]"));
	
	@Override
	protected Pattern getTokenizer() {		
		return tokenizer;
	}

	@Override
	protected Set<String> getIgnoreTokens() {	
		return ignoreTokens;
	}
}
