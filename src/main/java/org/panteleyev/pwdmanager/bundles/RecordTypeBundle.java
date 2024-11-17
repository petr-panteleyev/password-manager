/*
 Copyright © 2021-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
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
