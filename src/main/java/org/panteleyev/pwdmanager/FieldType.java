package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import java.util.ResourceBundle;

public enum FieldType {
    STRING,
    HIDDEN,
    EMAIL,
    CREDIT_CARD_NUMBER,
    LINK;

    private static final String BUNDLE = "org.panteleyev.pwdmanager.FieldType";

    private final String name;

    FieldType() {
        var bundle = ResourceBundle.getBundle(BUNDLE);
        this.name = bundle.getString(name());
    }

    @Override
    public String toString() {
        return name;
    }
}
