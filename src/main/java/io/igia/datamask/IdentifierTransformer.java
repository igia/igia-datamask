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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

public class IdentifierTransformer extends Transformer {
	static final Logger logger = Logger.getLogger(IdentifierTransformer.class);

	//add salt to prevent unmasking
	//need common salt to maintain IDs across files
	private static final String SALT = generateSalt();

	IdentifierTransformer() {
		super();
	}

	IdentifierTransformer(String xpath, String xpathTypeString, String rule, Map<String, String> params) {
		super(xpath, xpathTypeString, rule, params);
	}

	@Override
	public String mask(String value) {
		return md5Hash(value);
	}

	private static String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[20];
		random.nextBytes(salt);
		return Arrays.toString(salt);
	}

	// Takes a string, and converts it to md5 hashed string.
	private String md5Hash(String message) {
		String md5 = "";
		if (null == message)
			return null;

		message = message + SALT;// adding a salt to the string before it gets hashed.
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");// Create MessageDigest object for MD5
			digest.update(message.getBytes(), 0, message.length());// Update input string in message digest
			md5 = new BigInteger(1, digest.digest()).toString(16);// Converts message digest value in base 16 (hex)

		} catch (NoSuchAlgorithmException e) {
			logger.error("Unable to complete md5Hash.", e);
		}
		return md5;
	}

}
