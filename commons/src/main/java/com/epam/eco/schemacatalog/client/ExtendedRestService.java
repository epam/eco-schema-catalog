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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.entities.Config;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.ConfigUpdateRequest;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.RegisterSchemaRequest;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.RegisterSchemaResponse;
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
            String subject,
            boolean normalize,
            boolean lookupDeletedSchema)
            throws IOException, RestClientException {
        return super.lookUpSubjectVersion(
                requestProperties,
                registerSchemaRequest,
                encodeSubjectAsPathSegment(subject),
                normalize,
                lookupDeletedSchema);
    }

    @Override
    public RegisterSchemaResponse registerSchema(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject,
            boolean normalize) throws IOException, RestClientException {
        return super.registerSchema(
                requestProperties,
                registerSchemaRequest,
                encodeSubjectAsPathSegment(subject),
                normalize);
    }

    @Deprecated
    public boolean testCompatibility(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject,
            String version) throws IOException, RestClientException {
        return super.testCompatibility(
                requestProperties,
                registerSchemaRequest,
                encodeSubjectAsPathSegment(subject),
                version,
                false,
                false).isEmpty();
    }

    @Override
    public List<String> testCompatibility(
            Map<String, String> requestProperties,
            RegisterSchemaRequest registerSchemaRequest,
            String subject,
            String version,
            boolean normalize,
            boolean verbose) throws IOException, RestClientException {
        return super.testCompatibility(
                requestProperties,
                registerSchemaRequest,
                encodeSubjectAsPathSegment(subject),
                version,
                normalize,
                verbose);
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
    public Config getConfig(String subject)
            throws IOException, RestClientException {
        return getConfig(DEFAULT_REQUEST_PROPERTIES, subject, true);
    }

    @Override
    public Config getConfig(
            Map<String, String> requestProperties,
            String subject) throws IOException, RestClientException {
        return super.getConfig(
                requestProperties,
                encodeSubjectAsPathSegment(subject),
                true
        );
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
     * <p>
     * {@link RestService} simply concatenates subject (path segment) and other url parts w/o encoding, thus
     * any illegal character might cause request to fail with some misleading errors...
     */
    private static String encodeSubjectAsPathSegment(String subject) {
        if (subject == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        ByteBuffer bytes = StandardCharsets.UTF_8.encode(subject);
        while (bytes.hasRemaining()) {
            int ch = bytes.get() & 0xff;
            if (isPchar(ch)) {
                result.append((char) ch);
            } else {
                result.append('%');
                char hex1 = Character.toUpperCase(Character.forDigit((ch >> 4) & 0xF, 16));
                char hex2 = Character.toUpperCase(Character.forDigit(ch & 0xF, 16));
                result.append(hex1);
                result.append(hex2);
            }
        }
        return result.toString();
    }

    private static boolean isPchar(int ch) {
        return
                ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' ||
                ch >= '0' && ch <= '9' ||
                ch == '-' || ch == '.' || ch == '_' || ch == '~' ||
                ch == '!' || ch == '$' || ch == '&' || ch == '\'' || ch == '(' || ch == ')' || ch == '*' || ch == '+' || ch == ',' || ch == ';' || ch == '=' ||
                ch == ':' || ch == '@';
    }

}
