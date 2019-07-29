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

public class NumericIdentifierTransformerTest {

	@Test
	public void testDatamask() {
		String text = "123456789";
		Transformer transformer = new NumericIdentifierTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertEquals(text.length(), maskedValue.length());
		assertNotNull(Long.valueOf(maskedValue));
	}
	
	@Test
	public void testDatamaskSsn() {
		String text = "012-34-5678";
		Transformer transformer = new NumericIdentifierTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertEquals("999-99-9999", maskedValue);
	}
	
	@Test
	public void testDatamaskPhoneNumber() {
		String text = "(123)456-7890";
		Transformer transformer = new NumericIdentifierTransformer();
		String maskedValue = transformer.mask(text);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertEquals("(999)999-9999", maskedValue);
	}
}
