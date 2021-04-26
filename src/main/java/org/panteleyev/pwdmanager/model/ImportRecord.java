/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import java.util.Objects;

public class ImportRecord {
    private final ImportAction action;
    private final Card existingCard;
    private final Card cardToImport;
    private boolean approved = true;

    public ImportRecord(ImportAction action, Card existingCard, Card cardToImport) {
        this.action = action;
        this.existingCard = existingCard;
        this.cardToImport = cardToImport;
    }

    public ImportAction getAction() {
        return action;
    }

    public Card getExistingCard() {
        return existingCard;
    }

    public Card getCardToImport() {
        return cardToImport;
    }

    public boolean isApproved() {
        return approved;
    }

    public void toggleApproval() {
        approved = !approved;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImportRecord that) {
            return Objects.equals(this.action, that.action)
                && Objects.equals(this.existingCard, that.existingCard)
                && Objects.equals(this.cardToImport, that.cardToImport)
                && Objects.equals(this.approved, that.approved);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, existingCard, cardToImport, approved);
    }
}
