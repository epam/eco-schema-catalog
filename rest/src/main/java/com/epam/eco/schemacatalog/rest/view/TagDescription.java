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
package com.epam.eco.schemacatalog.rest.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Raman_Babich
 */
public final class TagDescription {

    private final String name;
    private final List<String> paramNames;
    private final List<Class<?>> paramTypes;
    private final String template;
    private final String description;

    public TagDescription(
            @JsonProperty("name") String name,
            @JsonProperty("paramNames") List<String> paramNames,
            @JsonProperty("paramTypes") List<Class<?>> paramTypes,
            @JsonProperty("template") String template,
            @JsonProperty("description") String description) {
        this.name = name;
        this.paramNames = paramNames == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(paramNames));
        this.paramTypes = paramTypes == null ? Collections.emptyList() : Collections.unmodifiableList(new ArrayList<>(paramTypes));
        this.template = template;
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public List<String> getParamNames() {
        return paramNames;
    }
    public List<Class<?>> getParamTypes() {
        return paramTypes;
    }
    public String getTemplate() {
        return template;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDescription that = (TagDescription) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(paramNames, that.paramNames) &&
                Objects.equals(paramTypes, that.paramTypes) &&
                Objects.equals(template, that.template) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, paramNames, paramTypes, template, description);
    }

    @Override
    public String toString() {
        return "TagDescription{" +
                "name='" + name + '\'' +
                ", paramNames=" + paramNames +
                ", paramTypes=" + paramTypes +
                ", template='" + template + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public Builder toBuilder() {
        return builder(this);
    }

    public static Builder builder() {
        return builder(null);
    }

    public static Builder builder(TagDescription description) {
        return new Builder(description);
    }

    public static class Builder {

        private String name;
        private List<String> paramNames;
        private List<Class<?>> paramTypes;
        private String template;
        private String description;

        public Builder(TagDescription description) {
            if (description == null) {
                return;
            }

            this.name = description.name;
            this.paramNames = new ArrayList<>(description.paramNames);
            this.paramTypes = new ArrayList<>(description.paramTypes);
            this.template = description.template;
            this.description = description.description;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder paramNames(List<String> paramNames) {
            this.paramNames = paramNames;
            return this;
        }

        public Builder addParamName(String paramName) {
            if (paramNames == null) {
                paramNames = new ArrayList<>();
            }
            this.paramNames.add(paramName);
            return this;
        }

        public Builder paramTypes(List<Class<?>> paramTypes) {
            this.paramTypes = paramTypes;
            return this;
        }

        public Builder addParamType(Class<?> paramType) {
            if (paramTypes == null) {
                paramTypes = new ArrayList<>();
            }
            this.paramTypes.add(paramType);
            return this;
        }

        public Builder template(String template) {
            this.template = template;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public TagDescription build() {
            return new TagDescription(name, paramNames, paramTypes, template, description);
        }
    }
}
