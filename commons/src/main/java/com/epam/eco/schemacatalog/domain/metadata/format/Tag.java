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
package com.epam.eco.schemacatalog.domain.metadata.format;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public class Tag implements Part {

    private final TagType type;
    private final List<Object> params;

    public Tag(TagType type, Collection<Object> params) {
        Validate.notNull(type, "Type is null");

        this.type = type;
        this.params =
                params != null ?
                Collections.unmodifiableList(new ArrayList<>(params)) :
                Collections.emptyList();
    }

    public TagType getType() {
        return type;
    }

    public List<Object> getParams() {
        return params;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, params);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Tag that = (Tag)obj;
        return
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.params, that.params);
    }

    @Override
    public String toString() {
        return
                TagParser.TAG_START +
                type.name().toLowerCase() +
                TagParser.TYPE_END +
                params.stream()
                        .map(Objects::toString)
                        .collect(Collectors.joining(TagParser.PARAM_DELIMITER)) +
                TagParser.TAG_END;
    }

}
