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

import io.igia.datamask.TextTransformer;
import io.igia.datamask.Transformer;

public class TextTransformerTest  {

	@Test
	public void testDatamask() {
		String text = "This is a test.";
		Transformer transformer = new TextTransformer();
		String maskedValue = transformer.mask(text);
		assertNotNull(maskedValue);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertEquals(text.length(), maskedValue.length());
	}

	@Test
	public void testDatamaskEmptyText() {
		String text = "";
		Transformer transformer = new TextTransformer();
		String maskedValue = transformer.mask(text);
		assertNotNull(maskedValue);
		assertTrue(maskedValue.equalsIgnoreCase(text));
		assertEquals(text.length(), maskedValue.length());
	}
}
