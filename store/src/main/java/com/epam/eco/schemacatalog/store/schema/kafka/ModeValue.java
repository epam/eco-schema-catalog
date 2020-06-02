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

import com.epam.eco.schemacatalog.domain.schema.Mode;

/**
 * @author Andrei_Tytsik
 */
public class ModeValue extends Value {

    private Mode mode;

    public ModeValue() {
        this(null);
    }

    public ModeValue(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ModeValue that = (ModeValue)obj;
        return
                Objects.equals(this.mode, that.mode);
    }

    @Override
    public String toString() {
        return
                "{mode: " + mode +
                "}";
    }

}
