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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

// abstract value transformer class for applying xpath rule transformations

public abstract class Transformer {

	static final Logger log = Logger.getLogger(Transformer.class);
	
	public enum NodeType {
		ATTRIBUTE, TEXT, JSON
	}

	private String path;
	private NodeType nodeType;
	private String rule;
	private Map<String, String> params = new HashMap<>();
	
	// global cache across all transformers
	protected static HashMap<String, String> datamaskCache = new HashMap<>();
	
	Transformer() {
		log.debug("Transformer() constructor");
	}

	Transformer(String path, String nodeTypeString, String rule, Map<String, String> params) {
		setPath(path);
		setNodeType(nodeTypeString);
		setRule(rule);
		setParams(params);
		log.debug("Transformer(path: "+path+", nodeTypeString: "+nodeTypeString+", rule: "+rule+") constructor");
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}
	public void setNodeType(String nodeTypeString) {
		setNodeType(NodeType.valueOf(nodeTypeString));
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public String getMaskedValue(String value) {
		log.debug("getMaskedValue("+value+")");
		if (value==null || value.length()==0) {
			log.debug("return null value");
			return "";
		}
		String result = datamaskCache.get(value);
		if (result==null) {
			result = mask(value);
			datamaskCache.put(value, result);
			log.debug("Added value to cache: '"+value+"' => '"+result+"'");
			return result;
		} else {
			log.debug("Found value in cache: '"+value+"' => '"+result+"'");
			return result;
		}
	}
	
	protected abstract String mask(String value);

}