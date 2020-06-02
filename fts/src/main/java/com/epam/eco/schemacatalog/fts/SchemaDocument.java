/*
 * Copyright 2020 EPAM Systems
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
package com.epam.eco.schemacatalog.fts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

/**
 * @author Andrei_Tytsik
 */
@Document(indexName=SchemaDocument.INDEX_NAME, type=SchemaDocument.TYPE)
@Setting(settingPath=SchemaDocument.SETTING_PATH)
@Mapping(mappingPath=SchemaDocument.MAPPING_PATH)
public final class SchemaDocument {

    public static final String INDEX_NAME = "schemacatalog_index";
    public static final String TYPE = "schema";
    public static final String SETTING_PATH = "/elasticsearch/schema_setting.json";
    public static final String MAPPING_PATH = "/elasticsearch/schema_mapping.json";

    @Id
    private String ecoId;

    private Integer schemaRegistryId;
    private String subject;
    private Integer version;
    private Boolean versionLatest;
    private String compatibility;
    private String mode;
    private String rootName;
    private String rootNamespace;
    private String rootFullname;
    private Boolean deleted;
    private Set<String> name;
    private Set<String> namespace;
    private Set<String> fullname;
    private Set<String> doc;
    private Set<String> logicalType;
    private Set<String> path;
    private Set<String> alias;
    private Set<KeyValue> property;
    private MetadataDocument metadata;

    public String getEcoId() {
        return ecoId;
    }
    public void setEcoId(String ecoId) {
        this.ecoId = ecoId;
    }
    public Integer getSchemaRegistryId() {
        return schemaRegistryId;
    }
    public void setSchemaRegistryId(Integer schemaRegistryId) {
        this.schemaRegistryId = schemaRegistryId;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public Integer getVersion() {
        return version;
    }
    public void setVersion(Integer version) {
        this.version = version;
    }
    public String getCompatibility() {
        return compatibility;
    }
    public void setCompatibility(String compatibility) {
        this.compatibility = compatibility;
    }
    public String getMode() {
        return mode;
    }
    public void setMode(String mode) {
        this.mode = mode;
    }
    public String getRootName() {
        return rootName;
    }
    public void setRootName(String rootName) {
        this.rootName = rootName;
    }
    public String getRootNamespace() {
        return rootNamespace;
    }
    public void setRootNamespace(String rootNamespace) {
        this.rootNamespace = rootNamespace;
    }
    public String getRootFullname() {
        return rootFullname;
    }
    public void setRootFullname(String rootFullname) {
        this.rootFullname = rootFullname;
    }
    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    public Set<String> getPath() {
        return path;
    }
    public void setPath(Set<String> path) {
        this.path = path;
    }
    public Set<String> getAlias() {
        return alias;
    }
    public void setAlias(Set<String> alias) {
        this.alias = alias;
    }
    public Set<String> getName() {
        return name;
    }
    public void setName(Set<String> name) {
        this.name = name;
    }
    public Set<String> getNamespace() {
        return namespace;
    }
    public void setNamespace(Set<String> namespace) {
        this.namespace = namespace;
    }
    public Set<String> getDoc() {
        return doc;
    }
    public void setDoc(Set<String> doc) {
        this.doc = doc;
    }
    public Set<String> getLogicalType() {
        return logicalType;
    }
    public void setLogicalType(Set<String> logicalType) {
        this.logicalType = logicalType;
    }
    public Set<KeyValue> getProperty() {
        return property;
    }
    public void setProperty(Set<KeyValue> property) {
        this.property = property;
    }
    public Set<String> getFullname() {
        return fullname;
    }
    public void setFullname(Set<String> fullname) {
        this.fullname = fullname;
    }
    public Boolean getVersionLatest() {
        return versionLatest;
    }
    public void setVersionLatest(Boolean versionLatest) {
        this.versionLatest = versionLatest;
    }
    public MetadataDocument getMetadata() {
        return metadata;
    }
    public void setMetadata(MetadataDocument metadata) {
        this.metadata = metadata;
    }

    public void addPath(String path) {
        path = StringUtils.stripToNull(path);
        if (path == null) {
            return;
        }

        if (this.path == null) {
            this.path = new HashSet<>();
        }
        this.path.add(path);
    }

    public void addAlias(String alias) {
        alias = StringUtils.stripToNull(alias);
        if (alias == null) {
            return;
        }

        if (this.alias == null) {
            this.alias = new HashSet<>();
        }
        this.alias.add(alias);
    }

    public void addName(String name) {
        name = StringUtils.stripToNull(name);
        if (name == null) {
            return;
        }

        if (this.name == null) {
            this.name = new HashSet<>();
        }
        this.name.add(name);
    }

    public void addNamespace(String namespace) {
        namespace = StringUtils.stripToNull(namespace);
        if (namespace == null) {
            return;
        }

        if (this.namespace == null) {
            this.namespace = new HashSet<>();
        }
        this.namespace.add(namespace);
    }

    public void addFullname(String fullname) {
        fullname = StringUtils.stripToNull(fullname);
        if (fullname == null) {
            return;
        }

        if (this.fullname == null) {
            this.fullname = new HashSet<>();
        }
        this.fullname.add(fullname);
    }

    public void addDoc(String doc) {
        doc = StringUtils.stripToNull(doc);
        if (doc == null) {
            return;
        }

        if (this.doc == null) {
            this.doc = new HashSet<>();
        }
        this.doc.add(doc);
    }

    public void addLogicalType(String logicalType) {
        logicalType = StringUtils.stripToNull(logicalType);
        if (logicalType == null) {
            return;
        }

        if (this.logicalType == null) {
            this.logicalType = new HashSet<>();
        }
        this.logicalType.add(logicalType);
    }

    public void addProperties(String key, Collection<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return;
        }

        values.forEach(value -> addProperty(key, value));
    }

    public void addProperty(String key, String value) {
        key = StringUtils.stripToNull(key);
        if (key == null) {
            return;
        }
        value = StringUtils.stripToNull(value);
        if (value == null) {
            return;
        }

        if (this.property == null) {
            this.property = new HashSet<>();
        }
        this.property.add(new KeyValue(key, value));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SchemaDocument that = (SchemaDocument) obj;
        return
                Objects.equals(ecoId, that.ecoId) &&
                Objects.equals(schemaRegistryId, that.schemaRegistryId) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(version, that.version) &&
                Objects.equals(versionLatest, that.versionLatest) &&
                Objects.equals(compatibility, that.compatibility) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(rootName, that.rootName) &&
                Objects.equals(rootNamespace, that.rootNamespace) &&
                Objects.equals(rootFullname, that.rootFullname) &&
                Objects.equals(deleted, that.deleted) &&
                Objects.equals(name, that.name) &&
                Objects.equals(namespace, that.namespace) &&
                Objects.equals(fullname, that.fullname) &&
                Objects.equals(doc, that.doc) &&
                Objects.equals(logicalType, that.logicalType) &&
                Objects.equals(path, that.path) &&
                Objects.equals(alias, that.alias) &&
                Objects.equals(property, that.property) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                ecoId,
                schemaRegistryId,
                subject,
                version,
                versionLatest,
                compatibility,
                mode,
                rootName,
                rootNamespace,
                rootFullname,
                deleted,
                name,
                namespace,
                fullname,
                doc,
                logicalType,
                path,
                alias,
                property,
                metadata);
    }

    @Override
    public String toString() {
        return
                "{ecoId: " + ecoId +
                ", schemaRegistryId: " + schemaRegistryId +
                ", subject: " + subject +
                ", version: " + version +
                ", versionLatest: " + versionLatest +
                ", compatibility: " + compatibility +
                ", mode: " + mode +
                ", rootName: " + rootName +
                ", rootNamespace: " + rootNamespace +
                ", rootFullname: " + rootFullname +
                ", deleted: " + deleted +
                ", name: " + name +
                ", namespace: " + namespace +
                ", fullname: " + fullname +
                ", doc: " + doc +
                ", logicalType: " + logicalType +
                ", path: " + path +
                ", alias: " + alias +
                ", property: " + property +
                ", metadata: " + metadata +
                "}";
    }

}
