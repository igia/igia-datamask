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

/**
 * Generate random text of equal length to source value.
 *
 */
public class TextTransformer extends Transformer {
	
	private static final String[] LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.".split(" ");
	
	TextTransformer() {
		super();
	}

	TextTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) {
		super(xpath, xpathTypeString, rule, params);
	}

	@Override
	public String mask(String value) {
		if (value==null || value.length()==0) return "";
		int length = value.length();
		StringBuilder result = new StringBuilder();
		int c = 0;
		while (true) {
			for (String s : LOREM_IPSUM) {
				result.append((c==0?"":" ") + s);
				if (result.length()==length) {
					return result.toString();
				}else if(result.length()>length){
					return result.substring(0, length);
				}
				c = 1;
			}
		}
	}

}
