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


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.rest.request.SchemaRequest;
import com.epam.eco.schemacatalog.domain.rest.response.VersionResponse;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Raman_Babich
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Config.class)
@Disabled("Manual, requires schema-registry running and schema catalog rest, see docker-compose in resources dir for the registry")
class SchemaCatalogClientIT {

    @Autowired
    private SchemaCatalogClient client;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    void testSchemaRegistryServiceInfoResolved() {
        SchemaRegistryServiceInfo info = client.getSchemaRegistryServiceInfo();

        assertNotNull(info);
    }

    @Test
    void accessesApi() {
        String subject = "rb.test-1";
        String schema = "{\n" +
                        "   \"type\" : \"record\",\n" +
                        "   \"namespace\" : \"namespace\",\n" +
                        "   \"name\" : \"Employee\",\n" +
                        "   \"fields\" : [\n" +
                        "      { \"name\" : \"Name\" , \"type\" : \"string\" },\n" +
                        "      { \"name\" : \"Age\" , \"type\" : \"int\" }\n" +
                        "   ]\n" +
                        "}";
        int version = 1;

        // /api/schemas
        SchemaRequest schemaRequest = new SchemaRequest(subject, schema);
        HttpEntity<SchemaRequest> request = new HttpEntity<>(schemaRequest);
        VersionResponse versionResponse = restTemplate
                .postForObject("http://localhost:8082/api/schemas", request, VersionResponse.class);

        assertNotNull(versionResponse);

        SearchParams searchParams = new SearchParams();
        searchParams.setQuery("*");
        SearchResult<LiteSchemaInfo> schemaInfos1 = client.searchLite(searchParams);

        assertNotNull(schemaInfos1);

        JsonSearchQuery jsonSearchQuery = new JsonSearchQuery("{\"match_all\" : {}}");
        SearchResult<LiteSchemaInfo> schemaInfos2 = client.searchLite(jsonSearchQuery);

        assertNotNull(schemaInfos2);

        FullSchemaInfo full1 = client.getFull(subject, version);

        assertNotNull(full1);

        MetadataUpdateParams params = MetadataUpdateParams.builder()
                .key(SchemaMetadataKey.with(subject, version))
                .appendAttribute("b", "b")
                .doc("doc")
                .build();
        client.updateMetadata(params);

        FullSchemaInfo full2 = client.getFull(subject, version);

        assertNotNull(full2);

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

        assertNotNull(full3);

        client.deleteMetadata(FieldMetadataKey.with(subject, version, "test", "a"));

        FullSchemaInfo full4 = client.getFull(subject, version);

        assertNotNull(full4);
    }

}
