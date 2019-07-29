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
/**
 * 
 */
package io.igia.datamask;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * @author mfo13
 *
 */

class OptionGroup {

	OptionGroup() {
		inputType = null;
		configFile = null; 
		schemaFile = null;
		inputFile = null;
		outputFile = null;
	}
	String inputType;
	String configFile;
	String schemaFile;
	String inputFile;
	String outputFile;
}

class Config {
	
	Config() {
		skipValidation = false;
		documents = new ArrayList<>();
	}
	Boolean skipValidation;
	List<SourceDocument<?>> documents;
}

public class Datamask {

	static final Logger log = Logger.getLogger(Datamask.class);

	
	private static Config config = new Config();

	public static void main(String[] args) {

		log.debug("igia-datamask starting");


		// Parse and process command line options
		CommandLine cmd = parseCommandLine(args);
		if (cmd == null) {
			System.exit(-1);
		}
		processCommandLine(config, cmd);

		//
		// Mask each requested file
		//
		for (SourceDocument<?> doc : config.documents) {
			log.debug("Processing: type=" + doc.type+ ", config=" + doc.configFile + ", schema=" + doc.schemaFile + ", input=" + doc.inputFile + ", output="
					+ doc.outputFile);
			doc.mask(config.skipValidation);
		}
		log.debug("igia-datamask ending");
	}
	
	private static CommandLine parseCommandLine(String[] args) 
	{
		// Usage:
		// <command> -m type:xml,config:example.xml,schema:example.xsd,in:example.xml,out:example-masked.xml
		// <command> --mask=type:xml,config:example.xml,schema:example.xsd,in:example.xml,out:example-masked.xml
		// <command> --skip-schema-validation --mask=type:xml,config:example.xml,schema:example.xsd,in:example.xml,out:example-masked.xml
		Option mOption = Option.builder("m").longOpt("mask").hasArg()
				.desc("Mask xml/json file using config and validate with schema.").build();
		Option xOption = Option.builder("x").longOpt("skip-schema-validation").desc("Skip validation with xsd/json schema.").build();
		Options options = new Options();
		options.addOption(mOption);
		options.addOption(xOption);
		CommandLineParser parser = new DefaultParser();
		try {
			return parser.parse(options, args);
		} catch (ParseException e) {
			log.error("Invalid command line option.");
			log.error(
					"Option must be in format: --mask=type:<xml|json>,config:example.xml,schema:example.xsd,in:example.xml,out:example-masked.xml [ --skip-schema-validation ]");
			return null;
		}
	}
	
	private static void processCommandLine(Config config, CommandLine cmd)
	{
		for (Option o : cmd.getOptions()) {
			if (o.getLongOpt().equalsIgnoreCase("skip-schema-validation")) {
				config.skipValidation = true;
			} else if (o.getLongOpt().equalsIgnoreCase("mask")) {
				processDatamaskOptionSet(o);
			} else {
				log.error("Invalid masking option '" + o.getLongOpt() + "'");
				System.exit(-1);
			}
		}
	}

	private static void processDatamaskOptionSet(Option o) {
		
		OptionGroup opts = new OptionGroup();

		String[] properties = o.getValue().split(",");
		for (String property : properties) {
			processDatamaskOptionProperty(opts, property);
		}
		// store away the option
		if (opts.inputType==null || opts.configFile==null || (config.skipValidation==false && opts.schemaFile==null) || opts.inputFile==null || opts.outputFile==null) {
			log.error("Insufficient data masking parameter keys specified; requires: config, schema, in, out, type");
			System.exit(-1);
		} else {
			if ("xml".equalsIgnoreCase(opts.inputType)) {
				config.documents.add(new XmlSourceDocument(opts.inputType, opts.configFile, opts.schemaFile, opts.inputFile, opts.outputFile));
			} else if ("json".equalsIgnoreCase(opts.inputType)) {
				config.documents.add(new JsonSourceDocument(opts.inputType, opts.configFile, opts.schemaFile, opts.inputFile, opts.outputFile));
			}
		}
	}

	private static void processDatamaskOptionProperty(OptionGroup opts, String property) {
		String[] p = property.split(":");
		if (p.length != 2) {
			log.error("Invalid command line option");
			System.exit(-1);
		}
		if (p[0].equalsIgnoreCase("config")) {
			opts.configFile = p[1];
		} else if (p[0].equalsIgnoreCase("schema")) {
			opts.schemaFile = p[1];
		} else if (p[0].equalsIgnoreCase("in")) {
			opts.inputFile = p[1];
		} else if (p[0].equalsIgnoreCase("out")) {
			opts.outputFile = p[1];
		} else if (p[0].equalsIgnoreCase("type")) {
			opts.inputType = p[1]!=null?p[1].toLowerCase():null;
			if (!"json".equalsIgnoreCase(opts.inputType) && !"xml".equalsIgnoreCase(opts.inputType)) {
				log.error("Invalid input file format type '" + opts.inputType + "'");
				System.exit(-1);
			}
		} else {
			log.error("Invalid data mask parameter key '" + p[0] + "'");
			System.exit(-1);
		}
	}
}
