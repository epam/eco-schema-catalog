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
import java.net.URI;
import java.net.URISyntaxException;
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
                encodeSubjectAsPathSegment(subject));
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
                encodeSubjectAsPathSegment(subject),
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
                encodeSubjectAsPathSegment(subject));
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
                encodeSubjectAsPathSegment(subject),
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
                encodeSubjectAsPathSegment(subject));
    }

    @Override
    public Config getConfig(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getConfig(
                requestProperties,
                encodeSubjectAsPathSegment(subject));
    }

    @Override
    public Schema getVersion(
            Map<String, String> requestProperties,
            String subject,
            int version) throws IOException, RestClientException {
        return super.getVersion(
                requestProperties,
                encodeSubjectAsPathSegment(subject),
                version);
    }

    @Override
    public Schema getLatestVersion(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getLatestVersion(
                requestProperties,
                encodeSubjectAsPathSegment(subject));
    }

    @Override
    public String getVersionSchemaOnly(
            String subject,
            int version) throws IOException, RestClientException {
        return super.getVersionSchemaOnly(encodeSubjectAsPathSegment(subject), version);
    }

    @Override
    public String getLatestVersionSchemaOnly(String subject) throws IOException, RestClientException {
        return super.getLatestVersionSchemaOnly(encodeSubjectAsPathSegment(subject));
    }

    @Override
    public List<Integer> getAllVersions(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getAllVersions(
                requestProperties,
                encodeSubjectAsPathSegment(subject));
    }

    @Override
    public Integer deleteSchemaVersion(
            Map<String, String> requestProperties,
            String subject,
            String version) throws IOException, RestClientException {
        return super.deleteSchemaVersion(
                requestProperties,
                encodeSubjectAsPathSegment(subject),
                version);
    }

    @Override
    public List<Integer> deleteSubject(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.deleteSubject(
                requestProperties,
                encodeSubjectAsPathSegment(subject));
    }

    /**
     * Workaround: encode subject to be used by {@link RestService} as path segment when building request URL.
     *
     * {@link RestService} simply concatenates subject (path segment) and other url parts w/o encoding, thus
     * any illegal character might cause request to fail with some misleading errors...
     */
    private static String encodeSubjectAsPathSegment(String subject) {
        try {
            return new URI(null, null, subject, null).toASCIIString();
        } catch (URISyntaxException use) {
            throw new RuntimeException(use);
        }
    }

}
