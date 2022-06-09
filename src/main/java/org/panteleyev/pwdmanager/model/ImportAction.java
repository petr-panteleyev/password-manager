/*
 Copyright © 2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

import org.panteleyev.pwdmanager.bundles.ImportActionBundle;

import java.util.ResourceBundle;

import static java.util.ResourceBundle.getBundle;

public enum ImportAction {
    SKIP,
    REPLACE,
    ADD,
    DELETE,
    RESTORE;

    private static final ResourceBundle BUNDLE = getBundle(ImportActionBundle.class.getCanonicalName());

    @Override
    public String toString() {
        return BUNDLE.getString(name());
    }
}
