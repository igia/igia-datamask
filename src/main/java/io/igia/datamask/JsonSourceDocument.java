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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class JsonSourceDocument extends SourceDocument<DocumentContext> {

	static final Logger log = Logger.getLogger(JsonSourceDocument.class);

	private String jsonText;

	private static final String JSON_V4_SCHEMA_IDENTIFIER = "http://json-schema.org/draft-04/schema#";
	private static final String JSON_SCHEMA_IDENTIFIER_ELEMENT = "$schema";
	
	private DocumentContext documentWithPathOption;

	JsonSourceDocument(String type, String config, String schema, String input, String output) {
		super(type, config, schema, input, output);
	}

	@Override
	public void maskDocument(Boolean skipValidation) {
		// open input file
		jsonText = readFileAsString(inputFile);

		// parse as json document
		Configuration pathConfiguration = Configuration.builder().options(Option.AS_PATH_LIST).build();
		document = JsonPath.parse(jsonText);
		// also open with read returning path lists option for searching
		documentWithPathOption = JsonPath.using(pathConfiguration).parse(jsonText);

		// validate against json schema if requested
		if (skipValidation) {
			log.warn("WARNING: Not validating the input file against a json schema.");
		} else {
			if (isValidJson()) {
				log.debug("File is valid for the specfied schema");
			} else {
				log.error("ERROR: Input file '" + inputFile + "' is not valid for the specified schema '" + schemaFile
						+ "'");
				throw new DatamaskException(
						"Input file '" + inputFile + "' is not valid for the specified schema '" + schemaFile + "'");
			}
		}

		// apply requested masking transforms
		for (Transformer t : config) {
			log.debug("Transforming path:" + t.getPath() + ", nodeType:" + t.getNodeType() + ", rule:" + t.getRule());
			try {
				transform(t);
			} catch (Exception e) {
				log.error("Failed to apply transformation :: rule: " + t.getRule() + ", path: " + t.getPath()
						+ ", type:" + t.getNodeType() + "; " + e.getMessage() + "; " + e.toString());
			}
		}

		// print output
		log.debug("About to read document for output streaming");
		String newJson = document.jsonString();

		// make it pretty
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(newJson);
		String prettyJsonString = gson.toJson(je);

		log.debug("Read document to string for output streaming");
		log.info(prettyJsonString);
		try (PrintStream ps = new PrintStream(outputFile)) {
			ps.println(prettyJsonString);
		} catch (FileNotFoundException e) {
			log.error("ERROR: failed to create output file '" + outputFile + "'; " + e.toString());
		}
	}

	private void transform(Transformer t) 
	{
		int c;
		log.debug("XmlSourceDocument.transform(path: " + t.getPath() + ", nodeType: " + t.getNodeType() + ", rule: "
				+ t.getRule() + ") constructor");
		// see if class has been correctly instantiated
		if (t.getRule() == null) {
			log.error("ERROR: unspecified rule; Transformer class instance missing path result type");
			throw new DatamaskException("Transformer class instance missing path result type.");
		}
		// for each path node, apply masking transformation
		c = 0;
		if (t.getNodeType() == io.igia.datamask.Transformer.NodeType.JSON) {
			log.debug("transform(): JSON");
			List<String> pathMatches = documentWithPathOption.read(t.getPath());
			for (String pathMatch : pathMatches) {
				log.debug("Process path:" + pathMatch);
				String value = document.read(pathMatch);
				// mask the element text
				document.set(pathMatch, t.getMaskedValue(value));
				c++;
			}
		} else {
			log.error("ERROR: unspecified path result type in rule");
			throw new DatamaskException("Transformer class instance missing path result type.");
		}
		log.debug("transform(): end; " + c + "items");
	}

	@Override
	protected void preConfigure() { 
		// not yet implemented for JSON but could be used in the future
		// the method is used by the XML source document class, and is helpful in the base class, so creating dummy here
	}

	@Override
	protected void postConfigure() { 
		// not yet implemented for JSON but could be used in the future
		// the method is used by the XML source document class, and is helpful in the base class, so creating dummy here
	}

	private Boolean isValidJson() {
		

		String schemaText = readFileAsString(schemaFile);
		try {
			// Prepare JSON Schema
			JsonNode node = JsonLoader.fromString(schemaText);
			JsonNode schemaIdentifier = node.get(JSON_SCHEMA_IDENTIFIER_ELEMENT);
			if (null == schemaIdentifier)
			{
				((ObjectNode) node).put(JSON_SCHEMA_IDENTIFIER_ELEMENT, JSON_V4_SCHEMA_IDENTIFIER);
			}

			JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			JsonSchema jsonSchemaNode = factory.getJsonSchema(node);

			// Prepare JSON text
			JsonNode jsonNode = JsonLoader.fromString(jsonText);

			// Validate JSON text versus JSON schema
			ProcessingReport report = jsonSchemaNode.validate(jsonNode);
			return report.isSuccess();

		} catch (ProcessingException e) {
			log.error("ERROR: Processing exception validating '"+inputFile+"' with schema '"+schemaFile+"'; "+e.toString());
			return false;
		} catch (IOException e) {
			log.error("ERROR: IO error validating '"+inputFile+"' with schema '"+schemaFile+"'; "+e.toString());
			return false;
		}
	}

	private String readFileAsString(String fileName) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			log.error("ERROR: failed to open file '" + fileName + "'; " + e.toString());
			return null;
		}
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
