/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

public record Field(FieldType type, String name, Object value) {
    public String getValueAsString() {
        return value.toString();
    }

    public boolean isEmpty() {
        return getValueAsString().isEmpty();
    }

    public String serializeValue() {
        // TODO: reimplement with switch pattern matching when available
        if (value instanceof Enum enumValue) {
            return enumValue.name();
        } else {
            return value.toString();
        }
    }

    public static Object deserializeValue(FieldType type, String value) {
        if (type == FieldType.CARD_TYPE) {
            return CardType.of(value);
        } else {
            return value;
        }
    }
}
