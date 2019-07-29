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

import java.io.IOException;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.igia.datamask.model.Config;
import io.igia.datamask.model.Field;

public class DatamaskTest {
	private static SecurityManager securityManager;
	private XmlDatamaskUtil util = new XmlDatamaskUtil();
	
	@BeforeClass
	public static void setUp() {
		securityManager = System.getSecurityManager();
	    System.setSecurityManager(new DisallowExitSecurityManager(securityManager));
	}
	
	@AfterClass
	public static void tearDown () {
		System.setSecurityManager(securityManager);
	}
	
	@Test
	public void testXml() throws JAXBException, JDOMException, IOException {
		OptionGroup opt = new OptionGroup();
		opt.configFile = "src/test/resources/example/config.xml";
		opt.inputType = "xml";
		opt.inputFile = "src/test/resources/example/employees.xml";
		opt.outputFile = "src/test/resources/example/employees-output-cli.xml";
		opt.schemaFile = "src/test/resources/example/employees.xsd";
		String[] args = new String[] { "-m", "type:" + opt.inputType + ",config:" + opt.configFile + ",schema:" + opt.schemaFile 
				+ ",in:" + opt.inputFile + ",out:" + opt.outputFile };
		Datamask.main(args);
		
		assertTrue(validateTransformation("src/test/resources/example/config.xml",
				"src/test/resources/example/employees.xml",
				"src/test/resources/example/employees-output-cli.xml"));
	}
	
	@Test
	public void testSkipSchemaValidation() throws JAXBException, JDOMException, IOException {
		OptionGroup opt = new OptionGroup();
		opt.configFile = "src/test/resources/example/config.xml";
		opt.inputType = "xml";
		opt.inputFile = "src/test/resources/example/employees.xml";
		opt.outputFile = "src/test/resources/example/employees-output-cli.xml";
		opt.schemaFile = "src/test/resources/example/employees.xsd";
		String[] args = new String[] { "--skip-schema-validation", "--mask=type:" + opt.inputType + ",config:" + opt.configFile
				+ ",in:" + opt.inputFile + ",out:" + opt.outputFile };
		Datamask.main(args);
		
		assertTrue(validateTransformation("src/test/resources/example/config.xml",
				"src/test/resources/example/employees.xml",
				"src/test/resources/example/employees-output-cli.xml"));
	}
	
	@Test(expected = SecurityException.class)
	public void testInvalidOption() {
		String[] args = new String[] {"--invalid"};
		Datamask.main(args);
	}
	
	@Test(expected = SecurityException.class)
	public void testMissingType() {
		String[] args = new String[] { "--mask=config:example.xml,schema:example.xsd,in:example.xml,out:example-masked.xml"};
		Datamask.main(args);
	}
	
	@Test(expected = SecurityException.class)
	public void testMissingConfig() {
		String[] args = new String[] { "--mask=type:xml,schema:example.xsd,in:example.xml,out:example-masked.xml"};
		Datamask.main(args);
	}
	
	@Test(expected = SecurityException.class)
	public void testMissingSchema() {
		String[] args = new String[] { "--mask=type:xml,config:example.xml,in:example.xml,out:example-masked.xml"};
		Datamask.main(args);
	}
	
	@Test(expected = SecurityException.class)
	public void testMissingInput() {
		String[] args = new String[] { "--mask=type:xml,config:example.xml,schema:example.xsd,out:example-masked.xml"};
		Datamask.main(args);
	}
	
	@Test(expected = SecurityException.class)
	public void testMissingOutput() {
		String[] args = new String[] { "--mask=type:xml,config:example.xml,schema:example.xsd,in:example.xml"};
		Datamask.main(args);
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
	
	public static class DisallowExitSecurityManager extends SecurityManager {
	    private final SecurityManager delegatedSecurityManager;

	    public DisallowExitSecurityManager(final SecurityManager originalSecurityManager) {
	        this.delegatedSecurityManager = originalSecurityManager;
	    }

	    @Override
	    public void checkExit(final int statusCode) {
	        if (delegatedSecurityManager != null) {
	            delegatedSecurityManager.checkExit(statusCode);
	        }else {
	        	super.checkExit(statusCode); // This is IMPORTANT!
	        }
            throw new SecurityException("Overriding shutdown...");
	    }

	    // Example:
	    @Override
	    public void checkPermission(Permission perm) {
	        if (delegatedSecurityManager != null) {
	            delegatedSecurityManager.checkPermission(perm);
	        }
	    }
	}
}
