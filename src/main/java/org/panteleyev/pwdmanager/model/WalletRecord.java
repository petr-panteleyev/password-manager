/*
 Copyright © 2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

import javafx.scene.input.DataFormat;

import java.util.Comparator;
import java.util.UUID;

public sealed interface WalletRecord permits Card, Note {
    DataFormat DATA_FORMAT = new DataFormat("application/x-org-panteleyev-password-manager-record-id");

    Comparator<WalletRecord> COMPARE_BY_NAME = Comparator.comparing(WalletRecord::name);
    Comparator<WalletRecord> COMPARE_BY_FAVORITE = Comparator.comparing(WalletRecord::favorite).reversed();
    Comparator<WalletRecord> COMPARE_BY_ACTIVE = Comparator.comparing(WalletRecord::active);

    UUID uuid();

    String name();

    String note();

    Picture picture();

    boolean favorite();

    boolean active();

    long modified();

    WalletRecord setFavorite(boolean favorite);

    WalletRecord setActive(boolean active);

    WalletRecord copyWithNewUuid();
}
