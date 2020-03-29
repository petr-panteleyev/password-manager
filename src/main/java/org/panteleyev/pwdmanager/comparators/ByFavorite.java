package org.panteleyev.pwdmanager.comparators;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.model.Card;
import java.util.Comparator;

public class ByFavorite implements Comparator<Card> {
    @Override
    public int compare(Card o1, Card o2) {
        return Boolean.compare(o2.favorite(), o1.favorite());
    }
}
