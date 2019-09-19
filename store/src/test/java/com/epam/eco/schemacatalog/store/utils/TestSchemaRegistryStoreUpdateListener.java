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
package com.epam.eco.schemacatalog.store.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.epam.eco.schemacatalog.store.schema.SchemaEntity;
import com.epam.eco.schemacatalog.store.schema.SchemaRegistryStoreUpdateListener;

/**
 * @author Andrei_Tytsik
 */
public class TestSchemaRegistryStoreUpdateListener implements SchemaRegistryStoreUpdateListener {

    private List<SchemaEntity> updated = new ArrayList<>();

    @Override
    public void onSchemasUpdated(Collection<SchemaEntity> schemas) {
        updated.addAll(schemas);
    }

    public List<SchemaEntity> getUpdated() {
        return updated;
    }

}
