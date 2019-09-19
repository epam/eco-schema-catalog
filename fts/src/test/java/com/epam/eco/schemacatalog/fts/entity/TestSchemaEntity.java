/*
 * Copyright 2019 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.epam.eco.schemacatalog.fts.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Yahor Urban
 */
public class TestSchemaEntity {

    private List<String> names;
    private List<String> namespaces;
    private List<String> fieldNames;
    private List<String> docs;
    private List<String> propKeys;
    private List<String> propValues;
    @SuppressWarnings("serial")
    private List<String> logicalTypes = new ArrayList<String>() {{
        add("time-millis");
        add("timestamp-millis");
        add("time-micros");
        add("timestamp-micros");
    }};

    private Integer id;
    private String subject;
//    private Integer version;

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public List<String> getDocs() {
        return docs;
    }

    public void setDocs(List<String> docs) {
        this.docs = docs;
    }

    public List<String> getPropKeys() {
        return propKeys;
    }

    public void setPropKeys(List<String> propKeys) {
        this.propKeys = propKeys;
    }

    public List<String> getPropValues() {
        return propValues;
    }

    public void setPropValues(List<String> propValues) {
        this.propValues = propValues;
    }

    public List<String> getLogicalTypes() {
        return logicalTypes;
    }

    public void setLogicalTypes(List<String> logicalTypes) {
        this.logicalTypes = logicalTypes;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

//    public Integer getVersion() {
//        return version;
//    }

//    public void setVersion(Integer version) {
//        this.version = version;
//    }
}
