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

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.xml.sax.SAXException;

public class XmlSourceDocument extends SourceDocument<Document> {

	static final Logger log = Logger.getLogger(XmlSourceDocument.class);

	public enum NodeType {
		ATTRIBUTE, TEXT
	}

	private XmlDatamaskUtil util = new XmlDatamaskUtil();
	private List<Namespace> namespaceList = new ArrayList<>();

	XmlSourceDocument(String type, String config, String schema, String input, String output) {
		super(type, config, schema, input, output);
	}

	private void transform(Transformer t) throws URISyntaxException {
		int c;
		log.debug("XmlSourceDocument.transform(path: " + t.getPath() + ", nodeType: " + t.getNodeType() + ", rule: " + t.getRule()
				+ ") constructor");
		// see if class has been correctly instantiated
		if (t.getRule() == null) {
			log.error("ERROR: unspecified rule; Transformer class instance missing path result type");
			throw new DatamaskException("Transformer class instance missing path result type.");
		}
		// for each path node, apply masking transformation
		switch (t.getNodeType()) {
		case ATTRIBUTE:
			log.debug("transform(): ATTRIBUTE");
			c = 0;
			for (Attribute att : util.getXpathAttributes(getDocument(), namespaceList, t.getPath())) {
				log.debug("We have target attr: " + att.getValue() + ", file: " + att.getParent().getXMLBaseURI()
						+ ", loc: " + getLocation(att.getParent()) + "/@" + att.getName());
				// mask the attribute
				att.setValue(t.getMaskedValue(att.getValue()));
				c++;
			}
			break;
		case TEXT:
			log.debug("transform(): TEXT");
			c = 0;
			for (Element elem : util.getXpathElements(getDocument(), namespaceList, t.getPath())) {
				log.debug("We have target element text: " + elem.getText() + "; file: " + elem.getXMLBaseURI()
						+ ", loc: " + getLocation(elem));
				// mask the element text
				elem.setText(t.getMaskedValue(elem.getText()));
				c++;
			}
			break;
		default:
			log.error("ERROR: unspecified path result type in rule");
			throw new DatamaskException("Transformer class instance missing path result type.");
		}
		log.debug("transform(): end; " + c + "items");
	}

	@Override
	public void maskDocument(Boolean skipValidation) {
		
		// Open xml input document, while confirming the document conforms to
		// the
		// specified XSD
		log.debug("Opening xml file '" + inputFile + "' with schema '" + schemaFile + "'");
		document = null;
		if (skipValidation) {
			log.warn("WARNING: Not validating the XML input file against an XSD.");
			try {
				document = util.getSAXParsedDocument(inputFile);
			} catch (JDOMException | IOException e) {
				log.error("Failed to load xml document: " + inputFile);
			}
		} else {
			try {
				document = util.getSAXParsedDocument(inputFile, schemaFile);
			} catch (JDOMException | IOException e) {
				log.error("Failed to load xml document: " + inputFile);
			} catch (SAXException e) {
				log.error("Failed to load xsd schema: " + schemaFile);
			}
		}
		if (document == null) {
			log.error("Error while opening the XML file" + (skipValidation ? "" : " and validating with XSD file"));
			return;
		}
		log.debug("Opened");

		// apply each path and rule transformation
		for (Transformer t : config) {
			log.debug("Transforming path:" + t.getPath() + ", nodeType:" + t.getNodeType() + ", rule:" + t.getRule());
			try {
				transform(t);
			} catch (Exception e) {
				log.error("Failed to apply transformation :: rule: " + t.getRule() + ", path: " + t.getPath()
						+ ", type:" + t.getNodeType()
						+ "; " + e.getMessage() + "; " + e.toString());
			}
		}

		// pretty print new xml
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		try {
			log.debug("Document: begin");
			out.output(document, new FileWriter(outputFile));
			log.info(out.outputString(document));
			log.debug("Document: end");
		} catch (IOException e) {
			log.error("Failed to generate output xml");
		}
	}

	@Override
	protected void postConfigure() {
		log.debug("Loading namespaces");
		namespaceList = new ArrayList<>();
		for (io.igia.datamask.model.Namespace ns : configXml.getNamespaces()) {
			Namespace namespace = Namespace.getNamespace(ns.getPrefix(), ns.getUrl());
			namespaceList.add(namespace);
		}
		for (Namespace n : namespaceList) {
			log.debug("namespace: " + n.getPrefix() + ":" + n.getURI());
		}
		log.debug("Loaded");
	}

	private String getLocation(Element elem) {
		StringBuilder location = new StringBuilder();
		Element tmpElem = elem;
		while (tmpElem != null) {
			location.insert(0,tmpElem.getName() + "/");
			tmpElem = tmpElem.getParentElement();
		}
		return location.toString();
	}

	@Override
	protected void preConfigure() {
		// preConfigure is available in the base class but not necessary for this subclass
		
	}
}
