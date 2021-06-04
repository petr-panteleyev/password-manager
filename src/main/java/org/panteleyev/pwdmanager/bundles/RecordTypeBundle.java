/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.bundles;

import java.util.ListResourceBundle;

public class RecordTypeBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
            {"CAR", "Car"},
            {"CREDIT_CARD", "Credit card"},
            {"EMAIL", "E-Mail"},
            {"EMPTY", "Empty"},
            {"GLASSES", "Glasses"},
            {"PASSPORT", "Passport"},
            {"PASSWORD", "Password"}
        };
    }
}
