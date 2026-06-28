// Copyright © 2017-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.model;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record Note(
        UUID uuid,
        String name,
        String note,
        boolean favorite,
        boolean active,
        long modified
) implements WalletRecord {

    @SuppressWarnings({"DataFlowIssue"})
    public Note {
        uuid = requireNonNull(uuid, "UUID must not be null");
        name = requireNonNull(name, "Name must not be null");

        note = note == null ? "" : note;
    }

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
