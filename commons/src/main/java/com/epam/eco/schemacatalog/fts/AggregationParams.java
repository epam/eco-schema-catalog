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

import java.util.Objects;


/**
 * @author Andrei_Tytsik
 */
public class AggregationParams {

    private String term;
    private String field;
    private int size;

    public AggregationParams() {
    }

    public AggregationParams(String term, String field, int size) {
        this.term = term;
        this.field = field;
        this.size = size;
    }

    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String getField() {
        return field;
    }
    public void setField(String field) {
        this.field = field;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

    public AggregationParams copyOf() {
        return new AggregationParams(term, field, size);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AggregationParams that = (AggregationParams) obj;
        return
                Objects.equals(this.term, that.term) &&
                Objects.equals(this.field, that.field) &&
                Objects.equals(this.size, that.size);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, field, size);
    }

    @Override
    public String toString() {
        return
                "{term: " + term +
                ", field: " + field +
                ", size: " + size +
                "}";
    }

}
