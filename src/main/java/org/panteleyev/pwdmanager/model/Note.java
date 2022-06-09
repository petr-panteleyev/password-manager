/*
 Copyright © 2017-2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

import java.util.UUID;

public record Note(
        UUID uuid,
        String name,
        String note,
        boolean favorite,
        boolean active,
        long modified
) implements WalletRecord {

    public Note(String name) {
        this(UUID.randomUUID(), name, "", false, true, System.currentTimeMillis());
    }

    public Note(UUID uuid, String name, String note, boolean favorite) {
        this(uuid, name, note, favorite, true, System.currentTimeMillis());
    }

    @Override
    public Picture picture() {
        return Picture.NOTE;
    }

    @Override
    public Note copyWithNewUuid() {
        return new Note(UUID.randomUUID(), name, note, favorite, active, System.currentTimeMillis());
    }

    @Override
    public Note setFavorite(boolean favorite) {
        return new Note(uuid, name, note, favorite, active, System.currentTimeMillis());
    }

    @Override
    public Note setActive(boolean active) {
        return new Note(uuid, name, note, favorite, active, System.currentTimeMillis());
    }
}
