/*
 Copyright © 2019-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
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
