/*
 *
 */
package com.epam.eco.schemacatalog.store.schema.kafka;

import java.util.Objects;

import com.epam.eco.schemacatalog.domain.schema.Mode;

/**
 * @author Andrei_Tytsik
 */
public class ModeValue extends Value {

    private Mode mode;

    public ModeValue() {
        this(null);
    }

    public ModeValue(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        ModeValue that = (ModeValue)obj;
        return
                Objects.equals(this.mode, that.mode);
    }

    @Override
    public String toString() {
        return
                "{mode: " + mode +
                "}";
    }

}