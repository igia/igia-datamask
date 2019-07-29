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

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.Test;

import io.igia.datamask.EmailTransformer;
import io.igia.datamask.Transformer;

public class EmailTransformerTest {

	@Test
	public void testDatamask() {
		String text = "neelima@igia.persistent.com";
		Transformer transformer = new EmailTransformer();
		String deidentifiedValue = transformer.mask(text);
		assertTrue(!deidentifiedValue.equalsIgnoreCase(text));		
		assertTrue(Pattern.matches("[a-z]{2,6}[0-9-_]{1,3}[a-z]{3,7}[@]{1}[a-zA-Z0-9]{3,10}[.]{1}(com|net|org|edu|gov|uk|ca|mil|co|biz|info|name|tech|io|cn|au|in|fr)", deidentifiedValue));		
	}
}
