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
package com.epam.eco.schemacatalog.store.schema;

import java.util.Objects;

import com.epam.eco.schemacatalog.domain.schema.Mode;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public final class SchemaEntity {

    private int id;
    private String subject;
    private int version;
    private CompatibilityLevel compatibilityLevel;
    private Mode mode;
    private String schema;
    private boolean versionLatest;
    private boolean deleted;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public CompatibilityLevel getCompatibilityLevel() {
        return compatibilityLevel;
    }
    public void setCompatibilityLevel(CompatibilityLevel compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }
    public Mode getMode() {
        return mode;
    }
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    public String getSchema() {
        return schema;
    }
    public void setSchema(String schema) {
        this.schema = schema;
    }
    public boolean isVersionLatest() {
        return versionLatest;
    }
    public void setVersionLatest(boolean versionLatest) {
        this.versionLatest = versionLatest;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SchemaEntity that = (SchemaEntity) obj;
        return
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.compatibilityLevel, that.compatibilityLevel) &&
                Objects.equals(this.mode, that.mode) &&
                Objects.equals(this.schema, that.schema) &&
                Objects.equals(this.versionLatest, that.versionLatest) &&
                Objects.equals(this.deleted, that.deleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                id,
                subject,
                version,
                compatibilityLevel,
                mode,
                schema,
                versionLatest,
                deleted);
    }

    @Override
    public String toString() {
        return
                "{id: " + id +
                ", subject: " + subject +
                ", version: " + version +
                ", compatibilityLevel: " + compatibilityLevel +
                ", mode: " + mode +
                ", schema: " + schema +
                ", versionLatest: " + versionLatest +
                ", deleted: " + deleted +
                "}";
    }

}
