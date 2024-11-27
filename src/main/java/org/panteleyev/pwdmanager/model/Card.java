/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
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
        if (note == null) {
            note = "";
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
