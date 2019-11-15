/*
 *
 */
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Objects;

/**
 * @author Andrei_Tytsik
 */
public class ModeKey extends Key {

    private static final int MAGIC_BYTE = 0;

    private String subject;

    public ModeKey() {
        this(null);
    }

    public ModeKey(String subject) {
        super(KeyType.MODE);

        this.subject = subject;
        this.magic = MAGIC_BYTE;
    }

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subject);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        ModeKey that = (ModeKey)obj;
        return
                Objects.equals(this.subject, that.subject);
    }

    @Override
    public String toString() {
        return
                "{magic: " + magic +
                ", keytype: " + keytype +
                ", subject: " + subject +
                "}";
    }

}