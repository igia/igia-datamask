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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransformerFactoryTest {
	TransformerFactory transformerFactory = new TransformerFactory();

	@Test
	public void testCreateDateOffsetTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.DATE_OFFSET.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof DateOffsetTransformer);
	}
	
	@Test
	public void testCreateZipCodeTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.ZIP_CODE.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof ZipCodeTransformer);
	}
	
	@Test
	public void testCreateBirthdateTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.BIRTHDATE.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof BirthdateTransformer);
	}
	
	@Test
	public void testCreateIdentifierTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.IDENTIFIER.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof IdentifierTransformer);
	}
	
	@Test
	public void testCreateNameTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.NAME.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof NameTransformer);
	}
	
	@Test
	public void testCreateNumericTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.NUMERIC.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof NumericTransformer);
	}
	
	@Test
	public void testCreateNumericIdentifierTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.NUMERIC_IDENTIFIER.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof NumericIdentifierTransformer);
	}
	
	@Test
	public void testCreateTextTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.TEXT.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof TextTransformer);
	}
	
	@Test
	public void testCreateEmailTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.EMAIL.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof EmailTransformer);
	}
	@Test
	public void testCreateIpAddressTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.IPADDRESS.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof IpAddressTransformer);
	}
	
	@Test
	public void testCreateTokenizeTextTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.TOKENIZE.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof TokenizeTextTransformer);
	}
	
	@Test
	public void testCreateUrlTransformer() {
		Transformer t = transformerFactory.create(TransformerFactory.SupportedTransformer.URL.getName(), null, Transformer.NodeType.TEXT.name(), null);
		assertTrue(t instanceof UrlTransformer);
	}
	
	@Test(expected = DatamaskException.class)
	public void testDatamaskExceptionNameNull() {
		transformerFactory.create(null, null, Transformer.NodeType.TEXT.name(), null);
	}
	
	@Test(expected = DatamaskException.class)
	public void testDatamaskExceptionNameNotFound() {
		transformerFactory.create("", null, Transformer.NodeType.TEXT.name(), null);
	}
}
