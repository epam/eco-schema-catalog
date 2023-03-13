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
package com.epam.eco.schemacatalog.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;

/**
 * @author Raman_Babich
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes=Config.class)
public class SchemaCatalogClientIT {

    @Autowired
    private SchemaCatalogClient client;

    @Test
    public void testSchemaRegistryServiceInfoResolved() {
        SchemaRegistryServiceInfo info = client.getSchemaRegistryServiceInfo();

        Assertions.assertNotNull(info);
    }

    @SuppressWarnings("unused")
    @Test
    public void testGeneralFunctionality() {
        String subject = "rb.test-1";
        int version = 1;

        SearchParams searchParams = new SearchParams();
        searchParams.setQuery("*");
        SearchResult<LiteSchemaInfo> schemaInfos1 = client.searchLite(searchParams);

        JsonSearchQuery jsonSearchQuery = new JsonSearchQuery("{\"match_all\" : {}}");
        SearchResult<LiteSchemaInfo> schemaInfos2 = client.searchLite(jsonSearchQuery);

        FullSchemaInfo full1 = client.getFull(subject, version);

        MetadataUpdateParams params = MetadataUpdateParams.builder()
                .key(SchemaMetadataKey.with(subject, version))
                .appendAttribute("b", "b")
                .doc("doc")
                .build();
        client.updateMetadata(params);

        FullSchemaInfo full2 = client.getFull(subject, version);

        MetadataBatchUpdateParams batch = MetadataBatchUpdateParams.builder()
                .delete(SchemaMetadataKey.with(subject, version))
                .update(
                        MetadataUpdateParams.builder()
                                .key(FieldMetadataKey.with(subject, version, "test", "a"))
                                .appendAttribute("b", "b")
                                .doc("text...text")
                                .build())
                .build();
        client.updateMetadata(batch);

        FullSchemaInfo full3 = client.getFull(subject, version);

        client.deleteMetadata(FieldMetadataKey.with(subject, version, "test", "a"));

        FullSchemaInfo full4 = client.getFull(subject, version);
    }

}
