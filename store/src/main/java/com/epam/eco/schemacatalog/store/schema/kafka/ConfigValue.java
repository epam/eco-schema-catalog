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

import io.confluent.kafka.schemaregistry.avro.AvroCompatibilityLevel;

/**
 * @author Andrei_Tytsik
 */
public class ConfigValue extends Value {

    private AvroCompatibilityLevel compatibilityLevel;

    public ConfigValue() {
        this(null);
    }

    public ConfigValue(AvroCompatibilityLevel compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }

    public AvroCompatibilityLevel getCompatibilityLevel() {
        return compatibilityLevel;
    }

    public void setCompatibilityLevel(AvroCompatibilityLevel compatibilityLevel) {
        this.compatibilityLevel = compatibilityLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(compatibilityLevel);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ConfigValue that = (ConfigValue)obj;
        return
                Objects.equals(this.compatibilityLevel, that.compatibilityLevel);
    }

    @Override
    public String toString() {
        return
                "{compatibilityLevel: " + compatibilityLevel +
                "}";
    }

}
