package org.panteleyev.pwdmanager.filters;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.model.Card;
import java.util.function.Predicate;

public class RecordNameFilter implements Predicate<Card> {
    private final String name;

    public RecordNameFilter(String name) {
        this.name = name;
    }

    @Override
    public boolean test(Card record) {
        return record.getName().toLowerCase().contains(name.toLowerCase());
    }
}
