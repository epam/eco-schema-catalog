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

import org.springframework.data.domain.Pageable;

/**
 * @author Andrei_Tytsik
 */
public final class JsonSearchQuery extends AbstractPagedQuery{

    private String json;

    public JsonSearchQuery() {
    }

    public JsonSearchQuery(String json) {
        this.json = json;
    }

    public JsonSearchQuery(String json, Pageable pageable) {
        super(pageable);

        this.json = json;
    }

    public JsonSearchQuery(String json, int page, int pageSize) {
        super(page, pageSize);

        this.json = json;
    }

    public String getJson() {
        return json;
    }
    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        JsonSearchQuery that = (JsonSearchQuery) obj;
        return
                Objects.equals(this.json, that.json) &&
                Objects.equals(this.getPage(), that.getPage()) &&
                Objects.equals(this.getPageSize(), that.getPageSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(json, getPage(), getPageSize());
    }

    @Override
    public String toString() {
        return
                "{json: " + json +
                ", page: " + getPage() +
                ", pageSize: " + getPageSize() +
                "}";
    }

}
