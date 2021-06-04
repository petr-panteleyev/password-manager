/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
