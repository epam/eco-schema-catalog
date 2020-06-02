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

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.domain.metadata.FieldMetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataBatchUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataType;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.rest.request.MetadataRequest;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.fts.JsonSearchQuery;
import com.epam.eco.schemacatalog.fts.SearchParams;
import com.epam.eco.schemacatalog.fts.SearchResult;

/**
 * @author Raman_Babich
 */
public class SchemaCatalogClientImpl implements SchemaCatalogClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaCatalogClientImpl.class);

    @Autowired
    @Qualifier("SchemaCatalogRestTemplate")
    private RestTemplate restTemplate;

    @Override
    public SchemaRegistryServiceInfo getSchemaRegistryServiceInfo() {
        return restTemplate.getForObject(
                        "/api/schemaregistry-info",
                        SchemaRegistryServiceInfo.class);
    }

    @Override
    public SearchResult<LiteSchemaInfo> searchLite(SearchParams params) {
        Validate.notNull(params, "Params is null");

        ResponseEntity<SearchResult<LiteSchemaInfo>> response = restTemplate.exchange(
                "/api/queries/schemas-by-params",
                HttpMethod.POST,
                new HttpEntity<>(params),
                new ParameterizedTypeReference<SearchResult<LiteSchemaInfo>>() {
                });

        return response.getBody();
    }

    @Override
    public SearchResult<LiteSchemaInfo> searchLite(JsonSearchQuery query) {
        Validate.notNull(query, "Query is null");

        ResponseEntity<SearchResult<LiteSchemaInfo>> response = restTemplate.exchange(
                "/api/queries/schemas-by-query",
                HttpMethod.POST,
                new HttpEntity<>(query.getJson()),
                new ParameterizedTypeReference<SearchResult<LiteSchemaInfo>>() {
                });

        return response.getBody();
    }

    @Override
    public FullSchemaInfo getFull(String subject, int version) {
        Validate.notBlank(subject, "Subject is blank");
        Validate.isTrue(version >= 0, "Version is negative");

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("subject", subject);
        uriVariables.put("version", version);

        return restTemplate.getForObject(
                "/api/schemas/{subject}/{version}",
                FullSchemaInfo.class,
                uriVariables);
    }

    @Override
    public void updateMetadata(MetadataUpdateParams params) {
        Validate.notNull(params, "Params object is null");

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("subject", params.getKey().getSubject());
        uriVariables.put("version", params.getKey().getVersion());

        String url;
        if (MetadataType.SCHEMA == params.getKey().getType()) {
            url = "/api/metadata/schemas/{subject}/{version}";
        } else if (MetadataType.FIELD == params.getKey().getType()) {
            url = "/api/metadata/schemas/{subject}/{version}/fields/{schemaFullName}/{field}";
            FieldMetadataKey fieldKey = (FieldMetadataKey) params.getKey();
            uriVariables.put("schemaFullName", fieldKey.getSchemaFullName());
            uriVariables.put("field", fieldKey.getField());
        } else {
            throw new IllegalArgumentException(
                    String.format("Metadata type '%s' is not supported", params.getKey().getType()));
        }

        MetadataRequest request = new MetadataRequest(params.getDoc(), params.getAttributes());

        restTemplate.put(
                url,
                request,
                uriVariables);
    }

    @Override
    public void updateMetadata(MetadataBatchUpdateParams params) {
        Validate.notNull(params, "Params can't be null");

        Map<String, MetadataRequest> requestMap = new HashMap<>();
        for (Map.Entry<MetadataKey, MetadataUpdateParams> entry : params.getOperations().entrySet()) {
            MetadataKey key = entry.getKey();
            MetadataUpdateParams value = entry.getValue();
            if (value == null) {
                requestMap.put(JsonMapper.toJson(key), null);
            } else {
                requestMap.put(
                        JsonMapper.toJson(key),
                        new MetadataRequest(value.getDoc(), value.getAttributes()));
            }
        }

        Map<String, String> response = restTemplate.exchange(
                "/api/metadata/schemas/$batch",
                HttpMethod.PUT,
                new HttpEntity<>(requestMap),
                new ParameterizedTypeReference<Map<String, String>>() {
                }).getBody();

        if (response != null && !response.isEmpty()) {
            StringJoiner logJoiner = new StringJoiner(",\n");
            response.forEach((key, value) -> logJoiner.add(key + " : " + value));
            LOGGER.error(logJoiner.toString());
            StringJoiner joiner = new StringJoiner(", ");
            response.keySet().forEach(joiner::add);
            throw new RuntimeException(
                    String.format("Can't update metadata for '%s' keys", joiner.toString()));
        }
    }

    @Override
    public void deleteMetadata(MetadataKey metadataKey) {
        Validate.notNull(metadataKey, "Metadata Key is null");

        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("subject", metadataKey.getSubject());
        uriVariables.put("version", metadataKey.getVersion());

        String url;
        if (MetadataType.SCHEMA == metadataKey.getType()) {
            url = "/api/metadata/schemas/{subject}/{version}";
        } else if (MetadataType.FIELD == metadataKey.getType()) {
            url = "/api/metadata/schemas/{subject}/{version}/fields/{schemaFullName}/{field}";
            FieldMetadataKey fieldKey = (FieldMetadataKey) metadataKey;
            uriVariables.put("schemaFullName", fieldKey.getSchemaFullName());
            uriVariables.put("field", fieldKey.getField());
        } else {
            throw new IllegalArgumentException(
                    String.format("Metadata type '%s' is not supported", metadataKey.getType()));
        }

        restTemplate.delete(
                url,
                uriVariables);
    }

}
