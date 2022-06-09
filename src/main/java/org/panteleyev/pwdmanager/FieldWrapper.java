/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
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
