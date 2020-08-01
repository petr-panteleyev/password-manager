/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.filters;

import org.panteleyev.pwdmanager.model.Card;
import java.util.function.Predicate;

public record RecordNameFilter(String name) implements Predicate<Card> {
    @Override
    public boolean test(Card record) {
        return record.name().toLowerCase().contains(name.toLowerCase());
    }
}
