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
package com.epam.eco.schemacatalog.fts.entity;

import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.convert.SchemaDocumentConverter;

/**
 * @author Yahor Urban
 */
public class FtsTestCase {

    private FullSchemaInfo schemaInfo;
    private SchemaDocument schemaDocument;
    private SearchParams searchParams;
    private String searchJsonQuery;

    public FtsTestCase(FullSchemaInfo schemaInfo, SearchParams searchParams) {
        this.schemaInfo = schemaInfo;
        this.schemaDocument = SchemaDocumentConverter.convert(schemaInfo);
        this.searchParams = searchParams;
    }

    public FtsTestCase(FullSchemaInfo schemaInfo, SchemaDocument schemaDocument, SearchParams searchParams) {
        this.schemaInfo = schemaInfo;
        this.schemaDocument = schemaDocument;
        this.searchParams = searchParams;
    }

    public FtsTestCase(FullSchemaInfo schemaInfo, SchemaDocument schemaDocument, String searchJsonQuery) {
        this.schemaInfo = schemaInfo;
        this.schemaDocument = schemaDocument;
        this.searchJsonQuery = searchJsonQuery;
    }

    public FullSchemaInfo getSchemaInfo() {
        return schemaInfo;
    }

    public void setSchemaInfo(FullSchemaInfo schemaInfo) {
        this.schemaInfo = schemaInfo;
    }

    public SchemaDocument getSchemaDocument() {
        return schemaDocument;
    }

    public void setSchemaDocument(SchemaDocument schemaDocument) {
        this.schemaDocument = schemaDocument;
    }

    public SearchParams getSearchParams() {
        return searchParams;
    }

    public void setSearchParams(SearchParams searchParams) {
        this.searchParams = searchParams;
    }

    public String getSearchJsonQuery() {
        return searchJsonQuery;
    }

    public void setSearchJsonQuery(String searchJsonQuery) {
        this.searchJsonQuery = searchJsonQuery;
    }
}
