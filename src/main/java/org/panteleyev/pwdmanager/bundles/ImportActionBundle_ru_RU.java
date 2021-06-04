/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.bundles;

import org.panteleyev.pwdmanager.model.ImportAction;
import java.util.ListResourceBundle;

@SuppressWarnings("unused")
public class ImportActionBundle_ru_RU extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {ImportAction.ADD.name(), "Добавить"},
            {ImportAction.DELETE.name(), "Удалить"},
            {ImportAction.REPLACE.name(), "Заменить"},
            {ImportAction.RESTORE.name(), "Восстановить"},
            {ImportAction.SKIP.name(), "Пропустить"}
        };
    }
}
