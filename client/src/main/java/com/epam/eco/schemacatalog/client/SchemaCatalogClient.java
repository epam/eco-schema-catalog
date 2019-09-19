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
package com.epam.eco.schemacatalog.client;

import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;

/**
 * @author Raman_Babich
 */
public interface SchemaCatalogClient {
    SchemaRegistryServiceInfo getSchemaRegistryServiceInfo();
    SearchResult<LiteSchemaInfo> searchLite(SearchParams params);
    SearchResult<LiteSchemaInfo> searchLite(JsonSearchQuery query);
    FullSchemaInfo getFull(String subject, int version);
    void updateMetadata(MetadataUpdateParams params);
    void updateMetadata(MetadataBatchUpdateParams params);
    void deleteMetadata(MetadataKey metadataKey);
}
