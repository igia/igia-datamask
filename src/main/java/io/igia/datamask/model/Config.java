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
package io.igia.datamask.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Config {
	
	@XmlAttribute(name = "sourceType")
	private String sourceType;

	@XmlAttribute(name = "schemaLocation")
	private String schemaLocation;

	@XmlElementWrapper(name = "fields")
	private List<Field> field = new ArrayList<>();

	@XmlElementWrapper(name = "namespaces")
	private List<Namespace> namespace = new ArrayList<>();
	
	public String getSourceType() {
		return sourceType;
	}
	
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}
	
	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public List<Field> getField() {
		return field;
	}

	public void setFields(List<Field> field) {
		this.field = field;
	}

	public List<Namespace> getNamespace() {
		return namespace;
	}

	public List<Namespace> getNamespaces() {
		return getNamespace();
	}

	public void setNamespace(List<Namespace> namespace) {
		this.namespace = namespace;
	}

}
