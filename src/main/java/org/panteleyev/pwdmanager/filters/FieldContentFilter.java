/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.filters;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.WalletRecord;
import java.util.function.Predicate;

public record FieldContentFilter(String value) implements Predicate<WalletRecord> {
    @Override
    public boolean test(WalletRecord record) {
        return !(record instanceof Card card) ||
            card.fields()
                .stream()
                .anyMatch(f -> f.getValueAsString().toLowerCase().contains(value.toLowerCase()));
    }
}
