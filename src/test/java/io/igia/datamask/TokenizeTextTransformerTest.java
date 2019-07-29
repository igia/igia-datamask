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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TokenizeTextTransformerTest {

	@Test
	public void testText() {		
		// put some items into the global cache
		String numeric = "123456789";
		Transformer numericTransformer = new NumericTransformer();
		String maskedNumeric = numericTransformer.getMaskedValue(numeric);
		
		String birthdate = "11/01/1950";
		Transformer birthdateTransformer = new BirthdateTransformer();
		String maskedBirthdate = birthdateTransformer.getMaskedValue(birthdate);

		// now numeric value should be replaced in tokenized target string such as a url
		String text = "Patient MRN 123456789 has birthdate 11/01/1950. SSN: 123456789?";
		Transformer textTransformer = new TokenizeTextTransformer();
		String maskedValue = textTransformer.getMaskedValue(text);
		
		assertNotNull(maskedValue);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertEquals(text.length(), maskedValue.length());
		
		assertTrue(maskedValue.equalsIgnoreCase("Patient MRN "+maskedNumeric+" has birthdate "+maskedBirthdate+". SSN: "+maskedNumeric+"?"));
	}
}
