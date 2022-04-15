/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;

final class FieldWrapper {
    private final Field field;
    private boolean show;

    FieldWrapper(Field field) {
        this.field = field;
    }

    Field getField() {
        return field;
    }

    void toggleShow() {
        show = !show;
    }

    boolean getShow() {
        return show;
    }

    String getName() {
        return field.name();
    }

    FieldType getType() {
        return field.type();
    }

    public Object getValue() {
        return field.value();
    }

    public String getValueAsString() {
        return field.getValueAsString();
    }
}
