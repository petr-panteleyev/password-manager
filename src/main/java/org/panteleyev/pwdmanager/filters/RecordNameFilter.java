/*
 Copyright © 2017-2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.filters;

import org.panteleyev.pwdmanager.model.WalletRecord;

import java.util.function.Predicate;

public record RecordNameFilter(String name) implements Predicate<WalletRecord> {
    @Override
    public boolean test(WalletRecord record) {
        return record.name().toLowerCase().contains(name.toLowerCase());
    }
}
