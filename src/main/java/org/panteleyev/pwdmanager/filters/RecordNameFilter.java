/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
