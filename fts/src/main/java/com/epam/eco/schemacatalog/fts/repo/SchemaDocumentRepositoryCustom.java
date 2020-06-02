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
package com.epam.eco.schemacatalog.fts.repo;

import org.springframework.data.elasticsearch.core.query.SearchQuery;

import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.QueryStringQuery;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;

/**
 * @author Andrei_Tytsik
 */
public interface SchemaDocumentRepositoryCustom {
    int getMaxResultWindow();
    SearchResult<SchemaDocument> searchByQuery(SearchQuery query);
    SearchResult<SchemaDocument> searchByQuery(JsonSearchQuery query);
    SearchResult<SchemaDocument> searchByQuery(QueryStringQuery query);
    SearchResult<SchemaDocument> searchByParams(SearchParams params);
}
