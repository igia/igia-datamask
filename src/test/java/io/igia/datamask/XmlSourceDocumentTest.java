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
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.junit.Test;

import io.igia.datamask.XmlDatamaskUtil;
import io.igia.datamask.XmlSourceDocument;
import io.igia.datamask.model.Config;
import io.igia.datamask.model.Field;

public class XmlSourceDocumentTest {
	private XmlDatamaskUtil util = new XmlDatamaskUtil();

	@Test
	public void testDeindentify() {
		mask("xml",
				"src/test/resources/example/config.xml", 
				"src/test/resources/example/employees.xml", 
				"src/test/resources/example/employees.xsd",
				"src/test/resources/example/employees-output.xml", 
				false);
		try {
			assertTrue(validateTransformation("src/test/resources/example/config.xml",
					"src/test/resources/example/employees.xml",
					"src/test/resources/example/employees-output.xml"));
		} catch (JAXBException | JDOMException | IOException e) {
			fail();
		}
	}

	@Test
	public void testDeindentifyExample() {
		mask("xml",
				"src/test/resources/example/config.xml", 
				"src/test/resources/example/employees.xml", 
				"src/test/resources/example/employees.xsd",
				"src/test/resources/example/employees-output.xml", 
				false);
		try {
			assertTrue(validateTransformation("src/test/resources/example/config.xml",
					"src/test/resources/example/employees.xml",
					"src/test/resources/example/employees-output.xml"));
		} catch (JAXBException | JDOMException | IOException e) {
			fail();
		}
	}
	
	@Test
	public void testDeindentifyFhirPatient() {
		mask("xml",
				"src/test/resources/fhir/patient/config.xml", 
				"src/test/resources/fhir/patient/patient-response.xml", 
				"src/test/resources/fhir/schema-xsd/patient.xsd",
				"src/test/resources/fhir/patient/patient-output.xml", 
				false);
		try {
			assertTrue(validateTransformation("src/test/resources/fhir/patient/config.xml",
					"src/test/resources/fhir/patient/patient-response.xml",
					"src/test/resources/fhir/patient/patient-output.xml"));
		} catch (JAXBException | JDOMException | IOException e) {
			fail();
		}
	}

	private void mask(String inputType, String xmlConfigFile, String xmlFile, String xsdFile, String outputFile, Boolean skipXsdValidation) {
		XmlSourceDocument doc = new XmlSourceDocument(inputType, xmlConfigFile, xsdFile, xmlFile, outputFile);
		doc.mask(skipXsdValidation);
	}

	private boolean validateTransformation(String xmlConfigFile, String xmlFile, String outputFile) throws JAXBException, JDOMException, IOException {
		Config config = util.unmarshallConfig(xmlConfigFile);
		Document input = util.getSAXParsedDocument(xmlFile);
		Document output = util.getSAXParsedDocument(outputFile);
		List<Namespace> namespaceList = new ArrayList<>();
		for(io.igia.datamask.model.Namespace namespace: config.getNamespace()) {
			Namespace ns = Namespace.getNamespace(namespace.getPrefix(), namespace.getUrl());
			namespaceList.add(ns);
		}
		
		//this assumes order remains the same in input and output files, 
		//may want to change this to search for any match in the output list
		for(Field field : config.getField()) {
			if(field.getType().equalsIgnoreCase("ATTRIBUTE")) {
				List<Attribute> inputAttributes = util.getXpathAttributes(input, namespaceList, field.getPath());
				List<Attribute> outputAttributes = util.getXpathAttributes(output, namespaceList, field.getPath());
				for(int i = 0; i < inputAttributes.size(); i++) {
					if(!inputAttributes.get(i).getValue().isEmpty() &&
							inputAttributes.get(i).getValue().equalsIgnoreCase(outputAttributes.get(i).getValue())) {
						return false;
					}
				}
			}else if(field.getType().equalsIgnoreCase("TEXT")) {
				List<Element> inputElements = util.getXpathElements(input, namespaceList, field.getPath());
				List<Element> outputElements = util.getXpathElements(output, namespaceList, field.getPath());
				for(int i = 0; i < inputElements.size(); i++) {
					if(!inputElements.get(i).getValue().isEmpty() &&
							inputElements.get(i).getValue().equalsIgnoreCase(outputElements.get(i).getValue())) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
}
