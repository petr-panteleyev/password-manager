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

@SuppressWarnings("unused")
public class FieldTypeBundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {CREDIT_CARD_NUMBER.name(), "Номер кредитной карты"},
                {EMAIL.name(), "E-mail"},
                {HIDDEN.name(), "Скрытая строка"},
                {LINK.name(), "Веб-адрес"},
                {LONG_PASSWORD.name(), "Длинный пароль"},
                {PIN.name(), "Пин код"},
                {SHORT_PASSWORD.name(), "Короткий пароль"},
                {STRING.name(), "Строка"},
                {UNIX_PASSWORD.name(), "Пароль UNIX"},
                {CARD_TYPE.name(), "Тип карты"},
                {DATE.name(), "Дата"},
                {EXPIRATION_MONTH.name(), "Месяц окончания"}
        };
    }
}
