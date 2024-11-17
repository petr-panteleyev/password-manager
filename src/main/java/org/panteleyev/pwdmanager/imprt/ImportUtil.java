/*
 Copyright © 2021-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.imprt;

import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.model.ImportRecord;
import org.panteleyev.pwdmanager.model.WalletRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class ImportUtil {
    private ImportUtil() {
    }

    public static List<ImportRecord> calculateImport(Collection<? extends WalletRecord> existing, Collection<?
            extends WalletRecord> toImport) {
        var result = new ArrayList<ImportRecord>(toImport.size());
        for (var card : toImport) {
            existing.stream()
                    .filter(x -> x.uuid().equals(card.uuid()))
                    .findAny()
                    .ifPresentOrElse(
                            found -> processUpdate(result, found, card),
                            () -> processAddition(result, card)
                    );
        }
        return result;
    }

    private static void processUpdate(List<ImportRecord> importRecords, WalletRecord existing, WalletRecord toImport) {
        if (existing.modified() < toImport.modified()) {
            var action = existing.active() == toImport.active() ?
                    ImportAction.REPLACE : toImport.active() ? ImportAction.RESTORE : ImportAction.DELETE;
            importRecords.add(new ImportRecord(action, existing, toImport));
        }
    }

    private static void processAddition(List<ImportRecord> importRecords, WalletRecord toImport) {
        if (toImport.active()) {
            // Inactive cards will not be added
            importRecords.add(new ImportRecord(toImport));
        }
    }
}
