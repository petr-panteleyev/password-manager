package org.panteleyev.pwdmanager.model;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.FieldType;
import java.util.Objects;

public class Field {
    private final FieldType type;
    private final String name;
    private final String value;

    public Field(FieldType type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    /**
     * Copy constructor.
     *
     * @param that copy from
     */
    public Field(Field that) {
        this.type = that.getType();
        this.name = that.getName();
        this.value = that.getValue();
    }

    public FieldType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Field) {
            var that = (Field) o;

            return Objects.equals(this.type, that.type)
                    && Objects.equals(this.name, that.name)
                    && Objects.equals(this.value, that.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, value);
    }

    @Override
    public String toString() {
        return "[Field:"
                + " type=" + type
                + " name=" + name
                + " value=" + value
                + "]";
    }
}
