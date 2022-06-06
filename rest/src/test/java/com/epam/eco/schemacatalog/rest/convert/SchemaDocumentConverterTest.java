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
package com.epam.eco.schemacatalog.rest.convert;

import org.junit.Assert;
import org.junit.Test;

import com.epam.eco.schemacatalog.domain.schema.LiteSchemaInfo;
import com.epam.eco.schemacatalog.domain.schema.Mode;
import com.epam.eco.schemacatalog.fts.MetadataDocument;
import com.epam.eco.schemacatalog.fts.SchemaDocument;
import com.epam.eco.schemacatalog.utils.EcoIdUtils;

import io.confluent.kafka.schemaregistry.CompatibilityLevel;

/**
 * @author Raman_Babich
 */
public class SchemaDocumentConverterTest {

    @Test
    public void testConvert() {
        SchemaDocument doc = new SchemaDocument();
        doc.setEcoId("ecoId");
        doc.setSchemaRegistryId(1);
        doc.setSubject("subject");
        doc.setVersion(2);
        doc.setVersionLatest(true);
        doc.setCompatibility("BACKWARD_TRANSITIVE");
        doc.setMode("IMPORT");
        doc.setRootName("rootName");
        doc.setRootNamespace("rootNamespace");
        doc.setRootFullname("rootFullname");
        doc.setDeleted(false);
        doc.addName("name-1");
        doc.addName("name-2");
        doc.addNamespace("namespace-1");
        doc.addNamespace("namespace-2");
        doc.addFullname("fullname-1");
        doc.addFullname("fullname-2");
        doc.addDoc("doc-1");
        doc.addDoc("doc-2");
        doc.addLogicalType("logicalType-1");
        doc.addLogicalType("logicalType-2");
        doc.addPath("path-1");
        doc.addPath("path-2");
        doc.addAlias("alias-1");
        doc.addAlias("alias-2");
        doc.addProperty("key-1", "value-1");
        doc.addProperty("key-2", "value-2");
        MetadataDocument metaDoc = new MetadataDocument();
        metaDoc.addDoc("doc-1");
        metaDoc.addDoc("doc-2");
        metaDoc.addAttribute("key-1", "object-1");
        metaDoc.addAttribute("key-2", "object-2");
        metaDoc.addUpdatedBy("updateBy-1");
        metaDoc.addUpdatedBy("updateBy-2");
        doc.setMetadata(metaDoc);

        LiteSchemaInfo schemaInfo = SchemaDocumentConverter.toLiteSchemaInfo(doc);

        Assert.assertEquals("subject", schemaInfo.getSubject());
        Assert.assertEquals(EcoIdUtils.formatId("subject", 2), schemaInfo.getEcoId());
        Assert.assertEquals(1, schemaInfo.getSchemaRegistryId());
        Assert.assertEquals(2, schemaInfo.getVersion());
        Assert.assertEquals("rootFullname", schemaInfo.getFullName());
        Assert.assertEquals("rootName", schemaInfo.getName());
        Assert.assertEquals("rootNamespace", schemaInfo.getNamespace());
        Assert.assertEquals(CompatibilityLevel.BACKWARD_TRANSITIVE, schemaInfo.getCompatibility());
        Assert.assertEquals(Mode.IMPORT, schemaInfo.getMode());
        Assert.assertTrue(schemaInfo.isVersionLatest());
        Assert.assertFalse(schemaInfo.isDeleted());
    }

}
