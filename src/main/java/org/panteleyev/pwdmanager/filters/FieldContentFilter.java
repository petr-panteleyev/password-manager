package org.panteleyev.pwdmanager.filters;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.model.Card;
import java.util.function.Predicate;

public class FieldContentFilter implements Predicate<Card> {
    private final String value;

    public FieldContentFilter(String value) {
        this.value = value;
    }

    @Override
    public boolean test(Card card) {
        return card.getFields().stream()
            .anyMatch(f -> f.getValue().toLowerCase().contains(value.toLowerCase()));
    }
}
