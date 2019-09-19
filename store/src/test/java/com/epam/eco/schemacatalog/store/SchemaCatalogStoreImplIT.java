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
package com.epam.eco.schemacatalog.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.junit4.SpringRunner;

import com.epam.eco.schemacatalog.domain.metadata.Metadata;
import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataUpdateParams;
import com.epam.eco.schemacatalog.domain.metadata.SchemaMetadataKey;
import com.epam.eco.schemacatalog.domain.schema.FullSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.SchemaCompatibilityCheckResult;
import com.epam.eco.schemacatalog.domain.schema.SchemaRegisterParams;
import com.epam.eco.schemacatalog.store.utils.TestSchemaCatalogStoreUpdateListener;

import static org.junit.Assert.assertFalse;

/**
 * @author Raman_Babich
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes=Config.class)
public class SchemaCatalogStoreImplIT {

    private static final String TARGET_SUBJECT = "03_avro-example_1-value";

    private static final int EVENTUAL_CONSISTENCY_SECONDS = 1;

    @Autowired
    private SchemaCatalogStore schemaCatalogStore;

    @Autowired
    private TestSchemaCatalogStoreUpdateListener updateListener;

    @Before
    public void beforeTest() throws Exception {
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(new TestingAuthenticationToken("principal", null));
        SecurityContextHolder.setContext(context);
    }

    @Test
    public void testAllWorksFine() throws Exception {
        List<FullSchemaInfo> updatedSchemas = updateListener.getUpdated();

        String subject = TARGET_SUBJECT;
        int version = 1;
        MetadataKey key = new SchemaMetadataKey(subject, version);
        String doc = "Some doc";
        Map<String, String> attributes = new HashMap<>();
        attributes.put("attr1", "attr1_value");
        attributes.put("attr2", "attr2_value");
        attributes.put("attr3", "attr3_value");

        updatedSchemas.clear();

        schemaCatalogStore.updateMetadata(MetadataUpdateParams.builder().
                key(key).
                doc(doc).
                attributes(attributes).
                build());
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSchemas.isEmpty());

        FullSchemaInfo schemaInfo = updatedSchemas.get(0);
        String updatedSubject = schemaInfo.getSubject();
        Assert.assertEquals(subject, updatedSubject);
        int updatedVersion = schemaInfo.getVersion();
        Assert.assertEquals(version, updatedVersion);
        Optional<Metadata> schemaMetadata = schemaInfo.getMetadataBrowser().getSchemaMetadata();
        Assert.assertTrue(schemaMetadata.isPresent());
        Assert.assertEquals(key, schemaMetadata.get().getKey());
        Assert.assertEquals(doc, schemaMetadata.get().getValue().getDoc());
        for (String attributeKey : attributes.keySet()) {
            Assert.assertEquals(
                    attributes.get(attributeKey),
                    schemaMetadata.get().getValue().getAttributes().get(attributeKey));
        }

        updatedSchemas.clear();
        schemaCatalogStore.deleteMetadata(key);
        TimeUnit.SECONDS.sleep(EVENTUAL_CONSISTENCY_SECONDS);

        assertFalse(updatedSchemas.isEmpty());

        schemaInfo = updatedSchemas.get(0);
        updatedSubject = schemaInfo.getSubject();
        Assert.assertEquals(subject, updatedSubject);
        updatedVersion = schemaInfo.getVersion();
        Assert.assertEquals(version, updatedVersion);
        Assert.assertFalse(schemaInfo.getMetadataBrowser().getSchemaMetadata().isPresent());
    }

    @Test
    public void testSchemaTestedForCompatibilityDetailed() throws Exception {
        String existingSchema = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"}]}";
        String incompatibleSchema = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":\"int\"}]}";
        String compatibleSchema = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"f1\",\"type\":\"int\"},{\"name\":\"f2\",\"type\":[\"null\",\"int\"],\"default\": null}]}";

        String subject = "test_detailed_compatibility_check_subject_" + System.currentTimeMillis();

        FullSchemaInfo existingSchemaInfo = schemaCatalogStore.registerSchema(
                SchemaRegisterParams.builder().
                    subject(subject).
                    schemaJson(existingSchema).
                    build());
        Assert.assertNotNull(existingSchemaInfo);

        SchemaCompatibilityCheckResult checkResult = schemaCatalogStore.testSchemaCompatibleDetailed(
                SchemaRegisterParams.builder().
                    subject(subject).
                    schemaJson(incompatibleSchema).
                    build());
        Assert.assertNotNull(checkResult);
        Assert.assertTrue(checkResult.hasErrors());

        checkResult = schemaCatalogStore.testSchemaCompatibleDetailed(
                SchemaRegisterParams.builder().
                    subject(subject).
                    schemaJson(compatibleSchema).
                    build());
        Assert.assertNotNull(checkResult);
        Assert.assertFalse(checkResult.hasErrors());
    }

}
