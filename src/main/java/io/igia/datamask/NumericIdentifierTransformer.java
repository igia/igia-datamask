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

import java.security.SecureRandom;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Masks identifying numbers such as SSNs, phone numbers, and zip codes.
 *
 */
public class NumericIdentifierTransformer extends Transformer {
	
	/*
	 * Support the following formats
     * SSN
	 * Phone Number
	 * Other Number
	 * 
	 */
	
	static final Logger logger = Logger.getLogger(NumericIdentifierTransformer.class);
	

	class NumericMatcher {
		String name;
		String example;
		String match;
		String formatString;
		Pattern pattern;
		Function<String, String> maskFunction;

		
		NumericMatcher(String name, String example, String match, Function<String, String> maskFunction) {
			this.name = name;
			this.example = example;
			this.match = match;
			this.pattern = Pattern.compile(this.match);
			this.maskFunction = maskFunction;
		}
	}
	
	private final NumericMatcher[] numericMatchList = {
		new NumericMatcher("SSN",      		"012-34-5678",	        "^\\d{3}-\\d{2}-\\d{4}$",                            this::ssnMasker),
		new NumericMatcher("Phone-Number",  "(123)456-7890", 		"^\\+\\d{12}|\\+\\d{11}$|" + // for country codes
																	"^\\(\\d{3}\\) \\d{3}-\\d{4}$|" + 
																	"^\\(\\d{3}\\)\\d{3}-\\d{4}$|" + 
																	"^\\(\\d{3}\\)-\\d{3}-\\d{4}$|" + 
																	"^\\(\\d{3}\\) \\d{3}.\\d{4}$|" +
																	"^\\(\\d{3}\\)\\.\\d{3}.\\d{4}$|" + 
																	"^\\d{3}\\.\\d{3}\\.\\d{4}$|" + 
																	"^\\d{3}-\\d{3}-\\d{4}$|" + 
																	"^\\d{3}-\\d{4}$|" +
																	"^\\d{3}\\.\\d{4}$",
																														 this::phoneMasker),
		//new NumericMatcher("Zip-Code",           "02332",           "^\\d{5}-\\d{4}$|^\\d{5}$",                          this::zipCodeMasker),
		// NOTE: must be last so we can catch zip code before
		new NumericMatcher("Number-Sequence",    "12345",           "\\d+",                                              this::numberSequenceMasker)
	};

	@Override
	public String mask(String value) {
		logger.debug("NumericIdentifierTransformer.deidentify(" + value + ")");

		NumericMatcher nm;
		Matcher matcher;
		if (value==null || value.length()==0) return "";
		
		// determine matching format type
		nm = null;
		for (NumericMatcher m : numericMatchList) {
			matcher = m.pattern.matcher(value);
			if (matcher.matches()) {
				nm = m;
				break;
			}
		}
		
		if (nm!=null) {	
			return nm.maskFunction.apply(value);
		} else {
			logger.error("Failed to match a number pattern; removing content '"+value+"'");
			return "";
		}
	}
	
	private String ssnMasker(String value) {
		return value.replaceAll("\\d", "9"); // "999-99-9999"
	}
	
	private String phoneMasker(String value) {
		return value.replaceAll("\\d", "9"); // "(999)999-9999"
	}
	
	private String numberSequenceMasker(String value) {
		String result;
		if (value==null || value.length()==0) {
			return "";
		}
		// use random number generator with similar digits, try again if equals
		long ran = new SecureRandom().nextLong() & Long.MAX_VALUE; // get positive random long
		result = String.valueOf(ran).substring(0,value.length());
		if (result.equalsIgnoreCase(value)) {
			return numberSequenceMasker(value);
		}
		return result;
	}
}
