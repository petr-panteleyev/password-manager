/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import java.util.List;
import java.util.UUID;

public record Card(
    UUID uuid,
    long modified,
    Picture picture,
    String name,
    List<Field> fields,
    String note,
    boolean favorite,
    boolean active
) implements WalletRecord {

    public Card {
        if (fields != null) {
            fields = List.copyOf(fields);
        }
    }

    public Card(String name, Picture picture, List<Field> fields) {
        this(UUID.randomUUID(), System.currentTimeMillis(), picture, name, fields, "", false, true);
    }

    @Override
    public Card copyWithNewUuid() {
        return new Card(UUID.randomUUID(), System.currentTimeMillis(), picture,
            name, fields, note, favorite, active);
    }

    @Override
    public Card setFavorite(boolean favorite) {
        return new Card(uuid, System.currentTimeMillis(), picture,
            name, fields, note, favorite, active);
    }

    @Override
    public Card setActive(boolean active) {
        return new Card(uuid, System.currentTimeMillis(), picture,
            name, fields, note, favorite, active);
    }
}
