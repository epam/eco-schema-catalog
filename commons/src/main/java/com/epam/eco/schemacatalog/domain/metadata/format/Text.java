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

import java.util.Objects;

import org.apache.commons.lang3.Validate;

/**
 * @author Andrei_Tytsik
 */
public class Text implements Part {

    public static final Text EMPTY = new Text("");

    private final String text;

    public Text(String text) {
        Validate.notNull(text, "Text is null");

        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Text that = (Text) obj;
        return
                Objects.equals(text, that.text);
    }

    @Override
    public String toString() {
        return text;
    }

}
