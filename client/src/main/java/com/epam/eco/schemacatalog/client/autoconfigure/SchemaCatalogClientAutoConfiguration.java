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
package com.epam.eco.schemacatalog.client.autoconfigure;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.epam.eco.commons.json.JsonMapper;
import com.epam.eco.schemacatalog.client.SchemaCatalogClient;
import com.epam.eco.schemacatalog.client.SchemaCatalogClientImpl;
import com.epam.eco.schemacatalog.domain.exception.NotFoundException;
import com.epam.eco.schemacatalog.domain.rest.response.MessageResponse;

/**
 * @author Raman_Babich
 */
@Configuration
@EnableConfigurationProperties({SchemaCatalogClientProperties.class})
@Import({
    DisabledClientSecurityConfiguration.class})
public class SchemaCatalogClientAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaCatalogClientAutoConfiguration.class);

    private static final String DEFAULT_MESSAGE_RESPONSE_CONTENT =
            "Message is not available, because it doesn't appear in the response body.";

    @Autowired
    private SchemaCatalogClientProperties properties;

    @Autowired
    @Qualifier("SchemaCatalogRestTemplate")
    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(responseErrorHandler());
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(properties.getSchemaCatalogUrl()));
    }

    @Bean
    public SchemaCatalogClient schemaCatalogClient() {
        return new SchemaCatalogClientImpl();
    }

    private ResponseErrorHandler responseErrorHandler() {
        return new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    MessageResponse messageResponse = toMessageResponse(response);
                    throw new NotFoundException(messageResponse.getMessage());
                } else if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
                    MessageResponse messageResponse = toMessageResponse(response);
                    throw new IllegalArgumentException(messageResponse.getMessage());
                }
                super.handleError(response);
            }
        };
    }

    private MessageResponse toMessageResponse(ClientHttpResponse response) {
        try {
            return JsonMapper.inputStreamToObject(
                    response.getBody(),
                    MessageResponse.class);
        } catch (IOException ex) {
            LOGGER.debug("Error occurs while trying to parse client response body.", ex);
        }
        return MessageResponse.with(DEFAULT_MESSAGE_RESPONSE_CONTENT);
    }

}
