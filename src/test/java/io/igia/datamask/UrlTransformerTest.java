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

public class UrlTransformerTest {

	@Test
	public void testUrl() {
		
		// put something into the global cache
		String numeric = "123456789";
		Transformer numericTransformer = new NumericTransformer();
		String maskedNumeric = numericTransformer.getMaskedValue(numeric);
		
		// now numeric value should be replaced in tokenized target string such as a url
		String text = "https://fhir.hl7.org/Patient/123456789";
		Transformer transformer = new UrlTransformer();
		String maskedValue = transformer.getMaskedValue(text);
		
		assertNotNull(maskedValue);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertEquals(text.length(), maskedValue.length());
		
		assertTrue(maskedValue.equalsIgnoreCase("https://fhir.hl7.org/Patient/"+maskedNumeric));
	}
}
