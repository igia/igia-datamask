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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.junit.Test;
import org.xml.sax.SAXException;

import io.igia.datamask.model.Config;

public class XmlDatamaskUtilTest {
	private XmlDatamaskUtil util = new XmlDatamaskUtil();

	@Test
	public void testUnmarshallConfig() {
		Config config = util.unmarshallConfig("src/test/resources/example/config.xml");
		assertEquals(9, config.getField().size());
	}
	
	@Test
	public void testGetSAXParsedDocument() throws JDOMException, IOException {
		Document input = util.getSAXParsedDocument("src/test/resources/example/employees.xml");
		assertEquals("employees", input.getRootElement().getName());
	}
	
	@Test
	public void testGetSAXParsedDocumentWithXsd() throws JDOMException, IOException, SAXException {
		Document input = util.getSAXParsedDocument("src/test/resources/example/employees.xml", "src/test/resources/example/employees.xsd");
		assertEquals("employees", input.getRootElement().getName());
	}
	
	@Test(expected = SAXException.class)
	public void testGetSAXParsedDocumentWithMissingXsd() throws JDOMException, IOException, SAXException {
		Document input = util.getSAXParsedDocument("src/test/resources/example/employees.xml", "");
		assertEquals("employees", input.getRootElement().getName());
	}
	
	@Test
	public void testGetXpathElements() throws JDOMException, IOException, SAXException {
		Config config = util.unmarshallConfig("src/test/resources/example/config.xml");
		Document input = util.getSAXParsedDocument("src/test/resources/example/employees.xml");
		
		List<Namespace> namespaceList = new ArrayList<>();
		for(io.igia.datamask.model.Namespace namespace: config.getNamespace()) {
			Namespace ns = Namespace.getNamespace(namespace.getPrefix(), namespace.getUrl());
			namespaceList.add(ns);
		}
		
		List<Element> elements = util.getXpathElements(input, namespaceList, "//employees/employee");
		assertEquals(2, elements.size());
	}
	
	@Test
	public void testGetXpathAttributes() throws JDOMException, IOException, SAXException {
		Config config = util.unmarshallConfig("src/test/resources/example/config.xml");
		Document input = util.getSAXParsedDocument("src/test/resources/example/employees.xml");
		
		List<Namespace> namespaceList = new ArrayList<>();
		for(io.igia.datamask.model.Namespace namespace: config.getNamespace()) {
			Namespace ns = Namespace.getNamespace(namespace.getPrefix(), namespace.getUrl());
			namespaceList.add(ns);
		}
		
		List<Attribute> attributes = util.getXpathAttributes(input, namespaceList, "//employees/employee/@id");
		assertEquals(2, attributes.size());
	}
}
