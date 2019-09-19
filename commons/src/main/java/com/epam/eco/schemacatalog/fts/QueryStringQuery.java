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
package com.epam.eco.schemacatalog.fts;

import java.util.Objects;

import org.springframework.data.domain.Pageable;

/**
 * @author Andrei_Tytsik
 */
public final class QueryStringQuery extends AbstractPagedQuery{

    private String queryString;

    public QueryStringQuery() {
    }

    public QueryStringQuery(String queryString) {
        this.queryString = queryString;
    }

    public QueryStringQuery(String queryString, Pageable pageable) {
        super(pageable);

        this.queryString = queryString;
    }

    public QueryStringQuery(String queryString, int page, int pageSize) {
        super(page, pageSize);

        this.queryString = queryString;
    }

    public String getQueryString() {
        return queryString;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        QueryStringQuery that = (QueryStringQuery) obj;
        return
                Objects.equals(this.queryString, that.queryString) &&
                Objects.equals(this.getPage(), that.getPage()) &&
                Objects.equals(this.getPageSize(), that.getPageSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryString, getPage(), getPageSize());
    }

    @Override
    public String toString() {
        return
                "{query: " + queryString +
                ", page: " + getPage() +
                ", pageSize: " + getPageSize() +
                "}";
    }

}
