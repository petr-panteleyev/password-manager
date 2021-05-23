/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

public record ImportRecord(ImportAction action, WalletRecord existingCard, WalletRecord cardToImport, boolean approved) {

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
