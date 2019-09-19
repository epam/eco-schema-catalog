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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.entities.Config;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.ConfigUpdateRequest;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.RegisterSchemaRequest;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.schemaregistry.client.rest.utils.UrlList;

/**
 * @author Andrei_Tytsik
 */
public class ExtendedRestService extends RestService {

    public ExtendedRestService(List<String> baseUrls) {
        super(baseUrls);
    }

    public ExtendedRestService(String baseUrlConfig) {
        super(baseUrlConfig);
    }

    public ExtendedRestService(UrlList baseUrls) {
        super(baseUrls);
    }

    public ExtendedRestService(RestService restService) {
        super(restService.getBaseUrls());
    }

    @Override
    public Schema lookUpSubjectVersion(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject)
            throws IOException, RestClientException {
        return super.lookUpSubjectVersion(
                requestProperties,
                registerSchemaRequest,
                encodeSubject(subject));
    }

    @Override
    public Schema lookUpSubjectVersion(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject,
            boolean lookupDeletedSchema) throws IOException, RestClientException {
        return super.lookUpSubjectVersion(
                requestProperties,
                registerSchemaRequest,
                encodeSubject(subject),
                lookupDeletedSchema);
    }

    @Override
    public int registerSchema(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject) throws IOException, RestClientException {
        return super.registerSchema(
                requestProperties,
                registerSchemaRequest,
                encodeSubject(subject));
    }

    @Override
    public boolean testCompatibility(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject,
            String version) throws IOException, RestClientException {
        return super.testCompatibility(
                requestProperties,
                registerSchemaRequest,
                encodeSubject(subject),
                version);
    }

    @Override
    public ConfigUpdateRequest updateConfig(
            Map<String, String> requestProperties,
            ConfigUpdateRequest configUpdateRequest,
            String subject) throws IOException, RestClientException {
        return super.updateConfig(
                requestProperties,
                configUpdateRequest,
                encodeSubject(subject));
    }

    @Override
    public Config getConfig(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getConfig(
                requestProperties,
                encodeSubject(subject));
    }

    @Override
    public Schema getVersion(
            Map<String, String> requestProperties,
            String subject,
            int version) throws IOException, RestClientException {
        return super.getVersion(
                requestProperties,
                encodeSubject(subject),
                version);
    }

    @Override
    public Schema getLatestVersion(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getLatestVersion(
                requestProperties,
                encodeSubject(subject));
    }

    @Override
    public List<Integer> getAllVersions(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getAllVersions(
                requestProperties,
                encodeSubject(subject));
    }

    @Override
    public Integer deleteSchemaVersion(
            String subject,
            String version) throws IOException, RestClientException {
        return super.deleteSchemaVersion(
                encodeSubject(subject),
                version);
    }

    @Override
    public List<Integer> deleteSubject(String subject) throws IOException, RestClientException {
        return super.deleteSubject(encodeSubject(subject));
    }

    private static String encodeSubject(String subject) throws UnsupportedEncodingException {
        if (subject == null) {
            return subject;
        }
        return URLEncoder.encode(subject, StandardCharsets.UTF_8.name());
    }

}
