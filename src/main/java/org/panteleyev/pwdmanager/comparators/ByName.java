package org.panteleyev.pwdmanager.comparators;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.model.Card;
import java.util.Comparator;

public class ByName implements Comparator<Card> {
    @Override
    public int compare(Card o1, Card o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
