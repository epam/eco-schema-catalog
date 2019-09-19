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
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Objects;

/**
 * @author Andrei_Tytsik
 */
public abstract class Key {

    protected int magic;
    protected KeyType keytype;

    public Key() {
        this(null);
    }

    public Key(KeyType keytype) {
        this.keytype = keytype;
    }

    public int getMagic() {
        return magic;
    }
    public void setMagic(int magic) {
        this.magic = magic;
    }
    public KeyType getKeytype() {
        return keytype;
    }
    public void setKeytype(KeyType keytype) {
        this.keytype = keytype;
    }

    @Override
    public int hashCode() {
        return Objects.hash(magic, keytype);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Key that = (Key)obj;
        return
                Objects.equals(this.magic, that.magic) &&
                        Objects.equals(this.keytype, that.keytype);
    }

    @Override
    public String toString() {
        return
                "{magic: " + magic +
                ", keytype: " + keytype +
                "}";
    }

}
