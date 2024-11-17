/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;

public final class FieldWrapper {
    private final Field field;
    private boolean show;

    public FieldWrapper(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public void toggleShow() {
        show = !show;
    }

    public boolean getShow() {
        return show;
    }

    public String getName() {
        return field.name();
    }

    public FieldType getType() {
        return field.type();
    }

    public Object getValue() {
        return field.value();
    }

    public String getValueAsString() {
        return field.getValueAsString();
    }
}
