/*
 Copyright © 2021-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.bundles;

import org.panteleyev.pwdmanager.model.ImportAction;

import java.util.ListResourceBundle;

public class ImportActionBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {ImportAction.ADD.name(), "Add"},
                {ImportAction.DELETE.name(), "Delete"},
                {ImportAction.REPLACE.name(), "Replace"},
                {ImportAction.RESTORE.name(), "Restore"},
                {ImportAction.SKIP.name(), "Skip"}
        };
    }
}
