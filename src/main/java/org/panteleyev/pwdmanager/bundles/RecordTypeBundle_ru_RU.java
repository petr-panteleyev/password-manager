/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
