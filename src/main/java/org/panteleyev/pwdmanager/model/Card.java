// Copyright © 2017-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.model;

import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

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

    @SuppressWarnings({"DataFlowIssue"})
    public Card {
        uuid = requireNonNull(uuid, "UUID must not be null");
        name = requireNonNull(name, "Name must not be null");

        note = note == null ? "" : note;
        fields = fields == null ? List.of() : fields;
        picture = picture == null ? Picture.GENERIC : picture;
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
