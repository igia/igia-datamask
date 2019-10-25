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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

import io.igia.datamask.model.Config;
import io.igia.datamask.model.Field;

public class JsonSourceDocumentTest {
	private XmlDatamaskUtil util = new XmlDatamaskUtil();

	@Test
	public void testDeindentifyNoSchema() {
		mask("json",
				"src/test/resources/fhir/patient/config-json.xml", 
				"src/test/resources/fhir/patient/patient-input.json", 
				null,
				"src/test/resources/fhir/patient/patient-output.json", 
				true);
		assertTrue(validateTransformation("src/test/resources/fhir/patient/config-json.xml",
				"src/test/resources/fhir/patient/patient-input.json",
				"src/test/resources/fhir/patient/patient-output.json"));
	}
	
	private void mask(String inputType, String xmlConfigFile, String inputFile, String schema, String outputFile, Boolean skipXsdValidation) {
		JsonSourceDocument doc = new JsonSourceDocument(inputType, xmlConfigFile, schema, inputFile, outputFile);
		doc.mask(skipXsdValidation);
	}
	
	private boolean validateTransformation(String xmlConfigFile, String inputFile, String outputFile) {
		Config config = util.unmarshallConfig(xmlConfigFile);
		Configuration pathConfiguration = Configuration.builder().options(Option.AS_PATH_LIST).build();
		
		DocumentContext inputDocumentWithPath = JsonPath.using(pathConfiguration).parse(readFileAsString(inputFile));		
		
		DocumentContext inputDocument = JsonPath.parse(readFileAsString(inputFile));
		DocumentContext outputDocument = JsonPath.parse(readFileAsString(outputFile));
		
		for(Field field : config.getField()) {
			if(field.getType().equalsIgnoreCase("JSON")) {
				List<String> inputPaths = inputDocumentWithPath.read(field.getPath());				
				
				for (String pathMatch : inputPaths) {
					String input = inputDocument.read(pathMatch);
					String output = outputDocument.read(pathMatch);
					if(!input.isEmpty() &&
							input.equalsIgnoreCase(output)) {
							fail(field.getPath() + ": input '" + input + "', output '" + output + "'");
							return false;
						
					}
				}
			}
		}
		
		return true;
	}
	
	private String readFileAsString(String fileName) {
		byte[] encoded = null;
		try {
			encoded = Files.readAllBytes(Paths.get(fileName));
		} catch (IOException e) {
			return null;
		}
		return new String(encoded, StandardCharsets.UTF_8);
	}
}
