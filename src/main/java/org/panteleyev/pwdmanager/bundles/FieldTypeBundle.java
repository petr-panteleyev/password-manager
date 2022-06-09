/*
 Copyright © 2021-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.bundles;

import java.util.ListResourceBundle;

import static org.panteleyev.pwdmanager.model.FieldType.CARD_TYPE;
import static org.panteleyev.pwdmanager.model.FieldType.CREDIT_CARD_NUMBER;
import static org.panteleyev.pwdmanager.model.FieldType.DATE;
import static org.panteleyev.pwdmanager.model.FieldType.EMAIL;
import static org.panteleyev.pwdmanager.model.FieldType.EXPIRATION_MONTH;
import static org.panteleyev.pwdmanager.model.FieldType.HIDDEN;
import static org.panteleyev.pwdmanager.model.FieldType.LINK;
import static org.panteleyev.pwdmanager.model.FieldType.LONG_PASSWORD;
import static org.panteleyev.pwdmanager.model.FieldType.PIN;
import static org.panteleyev.pwdmanager.model.FieldType.SHORT_PASSWORD;
import static org.panteleyev.pwdmanager.model.FieldType.STRING;
import static org.panteleyev.pwdmanager.model.FieldType.UNIX_PASSWORD;

public class FieldTypeBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {CREDIT_CARD_NUMBER.name(), "Credit Card Number"},
                {EMAIL.name(), "E-mail"},
                {HIDDEN.name(), "Hidden String"},
                {LINK.name(), "Web Link"},
                {LONG_PASSWORD.name(), "Long Password"},
                {PIN.name(), "PIN code"},
                {SHORT_PASSWORD.name(), "Short Password"},
                {STRING.name(), "String"},
                {UNIX_PASSWORD.name(), "UNIX Password"},
                {CARD_TYPE.name(), "Card Type"},
                {DATE.name(), "Date"},
                {EXPIRATION_MONTH.name(), "Expiration Month"}
        };
    }
}
