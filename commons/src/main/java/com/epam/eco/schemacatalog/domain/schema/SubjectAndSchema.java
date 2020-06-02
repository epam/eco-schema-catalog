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
package com.epam.eco.schemacatalog.domain.schema;

import java.util.Objects;

import org.apache.avro.Schema;
import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public final class SubjectAndSchema {

    private final String subject;
    private final Schema schema;

    public SubjectAndSchema(String subject, Schema schema) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.notNull(schema, "Schema is null");

        this.subject = subject;
        this.schema = schema;
    }

    public String getSubject() {
        return subject;
    }
    public Schema getSchema() {
        return schema;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, schema);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        SubjectAndSchema that = (SubjectAndSchema)obj;
        return
                Objects.equals(this.subject, that.subject) &&
                Objects.equals(this.schema, that.schema);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                ", schema: " + schema +
                "}";
    }

    public static SubjectAndSchema with(String subject, Schema schema) {
        return new SubjectAndSchema(subject, schema);
    }

}
