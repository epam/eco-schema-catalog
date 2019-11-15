/*
 *
 */
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Objects;

/**
 * @author Andrei_Tytsik
 */
public class ClearSubjectValue extends Value {

    private String subject;

    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ClearSubjectValue that = (ClearSubjectValue)obj;
        return
                Objects.equals(this.subject, that.subject);
    }

    @Override
    public String toString() {
        return
                "{subject: " + subject +
                "}";
    }

}