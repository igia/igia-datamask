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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.valkryst.generator.MarkovNameGenerator;

public class NameTransformer extends Transformer {

	static final Logger logger = Logger.getLogger(NameTransformer.class);
	
	private static final int MIN_NAME_LENGTH = 5;
	private static final int MAX_NAME_LENGTH = 10;

	private MarkovNameGenerator nameGenerator;

	NameTransformer() throws URISyntaxException, IOException {
		super();
		logger.debug("Initializing NameTransformer()");
		initialize();
		logger.debug("Initialized");
	}

	NameTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) 
			throws IOException, URISyntaxException {
		super(xpath, xpathTypeString, rule, params);
		logger.debug("Initializing NameTransformer(?,?,?)");
		initialize();
		logger.debug("Initialized");
	}
	
	void initialize() throws IOException {
		List<String> trainingNames;
        try {
			// need to get this from a stream since we're packaging as jar
			trainingNames = new ArrayList<>(); 
			final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("Roman-Male.txt");
			BufferedReader r = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line=r.readLine()) != null) {
			    trainingNames.add(line);
			}
        }
        catch (Exception e) {
        	logger.error("Failed to read training names resource file 'Roman-Male.txt'; "+e.getMessage()+"; "+e.getCause());
        	throw e;
        }
        try {
			this.nameGenerator = new MarkovNameGenerator(trainingNames);
        }
        catch (Exception e) {
        	logger.error("Failed to create markov name generator; "+e.getMessage()+"; "+e.getCause());
        	throw e;
        }
	}

	@Override
	public String mask(String value) {
		StringBuilder result = new StringBuilder();
		if (value==null || value.length()==0) return "";
		int wordCount = value.split(" ").length;
		int commaCount = value.split(",").length-1;
		logger.debug("mask '"+value+"; words="+wordCount+"; commas="+commaCount);
		for (int w=0; w<wordCount && w<3; w++) {
			int len = new SecureRandom().nextInt(MAX_NAME_LENGTH - MIN_NAME_LENGTH)+MIN_NAME_LENGTH;
			String name = nameGenerator.generateName(len);
			result.append((w>0?" ":"") + 
				      (w==2?name.substring(0, 1):name)+ 
				      (w==0&&commaCount>0?",":""));
		}
		return result.toString();
	}

}
