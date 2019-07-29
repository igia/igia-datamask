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
/**
 * 
 */
package io.igia.datamask;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;

import io.igia.datamask.NameTransformer;
import io.igia.datamask.Transformer;

public class NameTransformerTest {

	@Test
	public void testDatamask() {
		String text = "This is a test.";
		Transformer transformer = null;
		try {
			transformer = new NameTransformer();
		} catch (URISyntaxException | IOException e) {
			fail();
		}
		String maskedValue = transformer.mask(text);
		assertNotNull(maskedValue);
		assertTrue(!maskedValue.equalsIgnoreCase(text));
		assertTrue(maskedValue.length() > 0);
	}

}
