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
package com.epam.eco.schemacatalog.store.metadata;

import java.util.Map;

import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;

/**
 * @author Andrei_Tytsik
 */
public interface MetadataStore {
    MetadataValue get(MetadataKey key);
    Map<MetadataKey, MetadataValue> getCollection(String subject, int version);
    void createOrReplace(MetadataKey key, MetadataValue value);
    void executeBatchUpdate(Map<MetadataKey, MetadataValue> batch);
    void delete(MetadataKey key);
    void deleteAll(String subject, int version);
    void registerListener(MetadataStoreUpdateListener listener);
}
