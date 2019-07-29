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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.igia.datamask.model.Config;
import io.igia.datamask.model.Field;

public abstract class SourceDocument<D> {

	static final Logger log = Logger.getLogger(SourceDocument.class);
	
	protected D document;
		
	protected String type; // xml or json format
	protected String configFile;
	protected String schemaFile; // xsd or json schema
	protected String inputFile;
	protected String outputFile;
	protected Config configXml;

	private XmlDatamaskUtil util = new XmlDatamaskUtil();
	protected List<Transformer> config;

	SourceDocument(String type, String configFile, String schemaFile, String inputFile, String outputFile) {
		this.type = type;
		this.configFile = configFile;
		this.schemaFile = schemaFile;
		this.inputFile = inputFile;
		this.outputFile = outputFile;
	}
	
	public D getDocument() {
		return document;
	}

	public void setDocument(D document) {
		this.document = document;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}

	public String getSchemaFile() {
		return schemaFile;
	}

	public void setSchemaFile(String schemaFile) {
		this.schemaFile = schemaFile;
	}

	public String getInputFile() {
		return inputFile;
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public void mask(Boolean skipValidation) {
		configure();
		maskDocument(skipValidation);
	}
	
	public abstract void maskDocument(Boolean skipValidation);
	
	// intended to be overridden in sub class
	protected abstract void preConfigure();

	// intended to be overridden in sub class
	protected abstract void postConfigure();
	
	protected void configure() {
		log.debug("Pre-configuration");
		preConfigure();
		log.debug("Completed Pre-configuration");
		log.debug("Loading configuration");
		configXml = util.unmarshallConfig(configFile);
		if (configXml==null) {
			log.error("Failed to load xml configuration document '"+configFile+"'");
			return;
		}
		log.debug("Loaded");
		
		// Setup transformation configuration
		log.debug("Create TransformerFactory");
		TransformerFactory transFac = new TransformerFactory();
		config = new ArrayList<>();

		// read config file namespaces

		// read config file with path and rule values
		Transformer c;
		for (Field field : configXml.getField()) {
			String path = field.getPath();
			String fieldType = field.getType();
			String transform = field.getTransform();
			Map<String, String> params = field.getParams();
			log.debug("Config:: path: " + path + ", type: " + fieldType + ", transform: " + transform);
			try {
				c = transFac.create(transform, path, fieldType, params);
				config.add(c);
			} catch (Exception e) {
				log.error("Failed to create Transformer class instance as configured; path: " + path + ", type: "
						+ fieldType + ", transform: " + transform + "; " + e.getMessage());
			}
		}
		log.debug("Post-configuration");
		postConfigure();
		log.debug("Completed Post-configuration");
	}
}
