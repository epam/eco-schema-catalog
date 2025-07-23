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
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Objects;

/**
 * @author Andrei_Tytsik
 */
public class SchemaValue extends Value {

    private String subject;
    private Integer version;
    private Integer id;
    private String schema;
    private boolean deleted;
    private Long createdTimestamp;
    private Long deletedTimestamp;

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
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getSchema() {
        return schema;
    }
    public void setSchema(String schema) {
        this.schema = schema;
    }
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Long getDeletedTimestamp() {
        return deletedTimestamp;
    }

    public void setDeletedTimestamp(Long deletedTimestamp) {
        this.deletedTimestamp = deletedTimestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, version, id, schema, deleted, createdTimestamp, deletedTimestamp);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SchemaValue that = (SchemaValue)obj;
        return
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.version, that.version) &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.schema, that.schema) &&
                Objects.equals(this.createdTimestamp, that.createdTimestamp) &&
                Objects.equals(this.deletedTimestamp, that.deletedTimestamp) &&
                Objects.equals(this.deleted, that.deleted);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", version: " + version +
                ", id: " + id +
                ", schema: " + schema +
                ", createdTimestamp: " + createdTimestamp +
                ", deletedTimestamp: " + deletedTimestamp +
                ", deleted: " + deleted +
                "}";
    }

}
