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
package com.epam.eco.schemacatalog.store.metadata;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.epam.eco.schemacatalog.domain.metadata.MetadataKey;
import com.epam.eco.schemacatalog.domain.metadata.MetadataValue;

/**
 * @author Raman_Babich
 */
public class InheritingMetadataContainer implements MetadataContainer {

    private final String subject;
    private final NavigableMap<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> container =
            new TreeMap<>();

    public InheritingMetadataContainer(String subject) {
        Validate.notBlank(subject, "Subject is blank");

        this.subject = subject;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public Map<MetadataKey, MetadataValue> getCollection(int version) {
        Validate.isTrue(version >= 0, "Version is negative");

        Map.Entry<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> entry =
                container.floorEntry(version);
        return
                entry != null ?
                Collections.unmodifiableMap(toMetadataCollection(entry.getValue())) :
                null;
    }

    @Override
    public MetadataValue get(MetadataKey key) {
        Validate.notNull(key, "Metadata key is null");

        Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> entry = findEntryByOriginKey(key);
        return entry != null ? entry.getValue() : null;
    }

    @Override
    public MetadataValue put(MetadataKey key, MetadataValue value) {
        Validate.notNull(key, "Metadata key is null");
        Validate.notNull(value, "Metadata value is null");
        validateKeyAcceptable(key);

        MetadataValue oldValue;
        Map<VersionIgnoringKeyWrapper, MetadataValue> metadata = container.get(key.getVersion());
        if (metadata == null) {
            metadata = new HashMap<>();
            container.put(key.getVersion(), metadata);
            receiveAllInheritance(key.getVersion(), metadata);
        }
        VersionIgnoringKeyWrapper keyWrapper = new VersionIgnoringKeyWrapper(key);

        oldValue = metadata.get(keyWrapper);
        if (oldValue != null) {
            metadata.remove(keyWrapper); // it's only to refresh key version.
        }
        metadata.put(keyWrapper, value);
        if (!Objects.equals(value, oldValue)) {
            giveInheritance(key.getVersion(), keyWrapper, value);
        }
        return oldValue;
    }

    @Override
    public MetadataValue remove(MetadataKey key) {
        Validate.notNull(key, "Metadata key is null");
        validateKeyAcceptable(key);

        return removeByOriginKey(key);
    }

    @Override
    public boolean isEmpty() {
        return container.isEmpty();
    }

    private MetadataValue removeByOriginKey(MetadataKey key) {
        MetadataValue oldValue = null;
        Map<VersionIgnoringKeyWrapper, MetadataValue> metadata = container.get(key.getVersion());
        if (metadata != null) {
            Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> entry =
                    findEntryByOriginKey(metadata, key);
            if (entry != null) {
                oldValue = metadata.remove(entry.getKey());
                if (!containsVersion(metadata, key.getVersion())) {
                    container.remove(key.getVersion());
                }

                takeBackInheritance(key.getVersion(), entry.getKey());
                giveInheritanceFromParent(entry.getKey());
            }
        }
        return oldValue;
    }

    private void receiveAllInheritance(
            Integer currentVersion,
            Map<VersionIgnoringKeyWrapper, MetadataValue> child) {
        Map.Entry<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> parent =
                container.lowerEntry(currentVersion);
        if (parent != null) {
            child.putAll(parent.getValue());
        }
    }

    private void giveInheritance(
            Integer currentVersion,
            VersionIgnoringKeyWrapper inheritanceKey,
            MetadataValue inheritanceValue) {
        Map.Entry<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> child =
                container.higherEntry(currentVersion);
        if (child != null && !ownsSuchInheritance(child, inheritanceKey)) {
            child.getValue().put(inheritanceKey, inheritanceValue);
            giveInheritance(child.getKey(), inheritanceKey, inheritanceValue);
        }
    }

    private void takeBackInheritance(
            Integer currentVersion,
            VersionIgnoringKeyWrapper inheritanceKey) {
        Map.Entry<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> child =
                container.higherEntry(currentVersion);
        if (child != null && containsOriginKey(child.getValue(), inheritanceKey.unwrap())) {
            child.getValue().remove(inheritanceKey);
            takeBackInheritance(child.getKey(), inheritanceKey);
        }
    }

    private void giveInheritanceFromParent(VersionIgnoringKeyWrapper inheritanceKey) {
        Map.Entry<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> parent =
                container.lowerEntry(inheritanceKey.getVersion());
        if (parent != null) {
            Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> newInheritance =
                    findEntry(parent.getValue(), inheritanceKey);
            if (newInheritance != null) {
                giveInheritance(parent.getKey(), newInheritance.getKey(), newInheritance.getValue());
            }
        }
    }

    private Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> findEntry(
            Map<VersionIgnoringKeyWrapper, MetadataValue> metadata,
            VersionIgnoringKeyWrapper key) {
        for (Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> entry : metadata.entrySet()) {
            if (entry.getKey().equals(key)) {
                return entry;
            }
        }
        return null;
    }

    private boolean containsOriginKey(
            Map<VersionIgnoringKeyWrapper, MetadataValue> metadata,
            MetadataKey key) {
        return findEntryByOriginKey(metadata, key) != null;
    }

    private Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> findEntryByOriginKey(
            MetadataKey key) {
        Map<VersionIgnoringKeyWrapper, MetadataValue> metadata = container.get(key.getVersion());
        return findEntryByOriginKey(metadata, key);
    }

    private Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> findEntryByOriginKey(
            Map<VersionIgnoringKeyWrapper, MetadataValue> metadata,
            MetadataKey key) {
        if (metadata != null) {
            for (Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> entry : metadata.entrySet()) {
                if (entry.getKey().unwrap().equals(key)) {
                    return entry;
                }
            }
        }
        return null;
    }

    private boolean ownsSuchInheritance(
            Map.Entry<Integer, Map<VersionIgnoringKeyWrapper, MetadataValue>> owner,
            VersionIgnoringKeyWrapper inheritanceKey) {
        Integer ownVersion = owner.getKey();
        Map<VersionIgnoringKeyWrapper, MetadataValue> metadata = owner.getValue();
        for (Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> entry : metadata.entrySet()) {
            if (entry.getKey().equals(inheritanceKey) && entry.getKey().getVersion() == ownVersion) {
                return true;
            }
        }
        return false;
    }

    private boolean containsVersion(
            Map<VersionIgnoringKeyWrapper, MetadataValue> metadata,
            int version) {
        for (Map.Entry<VersionIgnoringKeyWrapper, MetadataValue> entry : metadata.entrySet()) {
            if (entry.getKey().getVersion() == version) {
                return true;
            }
        }
        return false;
    }

    private Map<MetadataKey, MetadataValue> toMetadataCollection(
            Map<VersionIgnoringKeyWrapper, MetadataValue> metadata) {
        return metadata.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().unwrap(), Map.Entry::getValue));
    }

    private void validateKeyAcceptable(MetadataKey key) {
        if (!subject.equals(key.getSubject())) {
            throw new IllegalArgumentException(
                    String.format(
                            "Metadata key '%s' has unacceptable subject, container's subject is '%s'",
                            key, subject));
        }
    }

    private static class VersionIgnoringKeyWrapper {

        private final MetadataKey key;

        private VersionIgnoringKeyWrapper(MetadataKey key) {
            this.key = key;
        }

        public MetadataKey unwrap() {
            return key;
        }

        public int getVersion() {
            return key.getVersion();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            VersionIgnoringKeyWrapper that = (VersionIgnoringKeyWrapper) o;
            return EqualsBuilder.reflectionEquals(key, that.key, "version");
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(key, "version");
        }

    }

}
