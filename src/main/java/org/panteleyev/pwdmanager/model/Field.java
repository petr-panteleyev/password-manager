package org.panteleyev.pwdmanager.model;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.FieldType;

public record Field(FieldType type, String name, String value) {

    public Field(Field field) {
        this(field.type, field.name, field.value);
    }
}
