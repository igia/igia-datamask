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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderSchemaFactory;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import org.xml.sax.SAXException;

import io.igia.datamask.model.Config;

public class XmlDatamaskUtil {

	static final Logger log = Logger.getLogger(XmlDatamaskUtil.class);
	private final XPathFactory xpfac = XPathFactory.instance();

	protected XmlDatamaskUtil() {

	}

	public Config unmarshallConfig(String configFile) {
		log.debug("Loading configuration");
		try {
			File file = new File(configFile);
			JAXBContext jContext = JAXBContext.newInstance(Config.class);
			Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
			log.debug("Loaded");
			return (Config) unmarshallerObj.unmarshal(file);
		} catch (JAXBException e) {
			log.error("Failed to load xml configuration document: " + configFile, e);
			return null;
		}
	}

	public Document getSAXParsedDocument(String fileName, String xsdFile)
			throws JDOMException, IOException, SAXException {
		SchemaFactory schemafac = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = null;
		schema = schemafac.newSchema(new File(xsdFile));
		XMLReaderJDOMFactory factory = new XMLReaderSchemaFactory(schema);
		SAXBuilder builder = new SAXBuilder(factory);

		return builder.build(fileName);
	}

	public Document getSAXParsedDocument(String fileName) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		return builder.build(fileName);
	}

	public List<Element> getXpathElements(Document document, List<Namespace> namespaceList, String xpath) {
		XPathExpression<Element> xpElem;
		log.debug("getXpathElements(document:"+(document==null?"null":"ok")+", namespaceCount="+namespaceList.size()+", xpath:"+xpath+")");

		xpElem = xpfac.compile(xpath, Filters.element(), null,
				namespaceList.toArray(new Namespace[namespaceList.size()]));
		if (xpElem==null) {
			log.debug("xpElem is null");
			return Collections.emptyList();
		}
		return xpElem.evaluate(document);
	}

	public List<Attribute> getXpathAttributes(Document document, List<Namespace> namespaceList, String xpath) {
		XPathExpression<Attribute> xpAttr;

		xpAttr = xpfac.compile(xpath, Filters.attribute(), null,
				namespaceList.toArray(new Namespace[namespaceList.size()]));
		return xpAttr.evaluate(document);
	}
}
