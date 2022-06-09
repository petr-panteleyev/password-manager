/*
 Copyright © 2021-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.bundles;

import java.util.ListResourceBundle;

@SuppressWarnings("unused")
public class RecordTypeBundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"CAR", "Автомобиль"},
                {"CREDIT_CARD", "Кредитная карта"},
                {"EMAIL", "E-Mail"},
                {"EMPTY", "Пустая"},
                {"GLASSES", "Очки"},
                {"PASSPORT", "Паспорт"},
                {"PASSWORD", "Пароль"}
        };
    }
}
