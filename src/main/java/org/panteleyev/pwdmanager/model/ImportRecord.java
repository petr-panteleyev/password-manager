// Copyright © 2021-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.model;

import static java.util.Objects.requireNonNull;

public record ImportRecord(ImportAction action, WalletRecord existingCard, WalletRecord cardToImport,
                           boolean approved) {

    @SuppressWarnings("DataFlowIssue")
    public ImportRecord {
        action = requireNonNull(action, "Action must not be null");
        cardToImport = requireNonNull(cardToImport, "Card to import must not be null");
    }

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
