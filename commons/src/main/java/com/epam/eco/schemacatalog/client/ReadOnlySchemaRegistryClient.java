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
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;

import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;

/**
 * @author Andrei_Tytsik
 */
public class ReadOnlySchemaRegistryClient extends AbstractSchemaRegistryClientDecorator {

    public ReadOnlySchemaRegistryClient(SchemaRegistryClient schemaRegistryClient) {
        super(schemaRegistryClient);
    }

    @Override
    public final int register(String subject, Schema schema) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int register(String subject, Schema schema, int version, int id) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String updateCompatibility(String subject, String compatibility) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> deleteSubject(String subject) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Integer> deleteSubject(Map<String, String> requestProperties, String subject) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer deleteSchemaVersion(String subject, String version) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer deleteSchemaVersion(Map<String, String> requestProperties, String subject, String version) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setMode(String mode) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String setMode(String mode, String subject) throws IOException, RestClientException {
        throw new UnsupportedOperationException();
    }

}
