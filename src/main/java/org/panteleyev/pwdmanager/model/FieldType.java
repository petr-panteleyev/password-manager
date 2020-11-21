/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import static org.panteleyev.pwdmanager.Constants.FIELD_TYPE_BUNDLE;

public enum FieldType {
    STRING,
    HIDDEN(true),
    EMAIL,
    CREDIT_CARD_NUMBER,
    LINK,
    PIN(true),
    UNIX_PASSWORD(true),
    SHORT_PASSWORD(true),
    LONG_PASSWORD(true);

    private final String name;
    private final boolean masked;

    FieldType() {
        this.name = FIELD_TYPE_BUNDLE.getString(name());
        this.masked = false;
    }

    FieldType(boolean masked) {
        this.name = FIELD_TYPE_BUNDLE.getString(name());
        this.masked = masked;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean isMasked() {
        return masked;
    }
}
