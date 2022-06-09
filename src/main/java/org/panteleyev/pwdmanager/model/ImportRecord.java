/*
 Copyright © 2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

public record ImportRecord(ImportAction action, WalletRecord existingCard, WalletRecord cardToImport,
                           boolean approved) {

    public ImportRecord(ImportAction action, WalletRecord existingCard, WalletRecord cardToImport) {
        this(action, existingCard, cardToImport, true);
    }

    public ImportRecord(WalletRecord cardToImport) {
        this(ImportAction.ADD, null, cardToImport, true);
    }

    public ImportAction getEffectiveAction() {
        return approved ? action : ImportAction.SKIP;
    }

    public ImportRecord toggleApproval() {
        return new ImportRecord(action, existingCard, cardToImport, !approved);
    }
}
