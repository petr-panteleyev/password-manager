// Copyright © 2017-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.model;

import org.panteleyev.pwdmanager.bundles.FieldTypeBundle;

import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

public enum FieldType {
    STRING,
    HIDDEN(true),
    EMAIL,
    CREDIT_CARD_NUMBER,
    LINK,
    PIN(true),
    UNIX_PASSWORD(true),
    SHORT_PASSWORD(true),
    LONG_PASSWORD(true),
    CARD_TYPE,
    DATE,
    EXPIRATION_MONTH,
    TOTP_CODE(true);

    private static final ResourceBundle BUNDLE = getBundle(FieldTypeBundle.class.getCanonicalName());

    private final boolean masked;

    FieldType() {
        this(false);
    }

    FieldType(boolean masked) {
        this.masked = masked;
    }

    @Override
    public String toString() {
        return BUNDLE.getString(name());
    }

    public boolean isMasked() {
        return masked;
    }
}
