/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.bundles;

import org.panteleyev.pwdmanager.model.ImportAction;
import java.util.ListResourceBundle;

public class ImportActionBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            {ImportAction.ADD.name(), "Add"},
            {ImportAction.DELETE.name(), "Delete"},
            {ImportAction.REPLACE.name(), "Replace"},
            {ImportAction.RESTORE.name(), "Restore"},
            {ImportAction.SKIP.name(), "Skip"}
        };
    }
}
