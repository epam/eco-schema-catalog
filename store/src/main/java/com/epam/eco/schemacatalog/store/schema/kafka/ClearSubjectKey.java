/*
 *
 */
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Objects;

/**
 * @author Andrei_Tytsik
 */
public class ClearSubjectKey extends Key {

    private static final int MAGIC_BYTE = 0;

    private String subject;

    public ClearSubjectKey() {
        this(null);
    }

    public ClearSubjectKey(String subject) {
        super(KeyType.CLEAR_SUBJECT);

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

        ClearSubjectKey that = (ClearSubjectKey)obj;
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