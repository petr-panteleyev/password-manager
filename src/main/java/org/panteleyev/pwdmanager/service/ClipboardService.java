// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.service;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.panteleyev.pwdmanager.model.WalletRecord;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

/// Service responsible for managing clipboard operations for wallet records.
///
/// Encapsulates all interactions with the system clipboard, including copying record UUIDs and checking/pasting
/// clipboard content.
public final class ClipboardService {

    /// Checks whether the system clipboard contains a wallet record UUID.
    ///
    /// @return `true` if the clipboard contains content matching the record data format
    public boolean isClipboardHasRecord() {
        return Clipboard.getSystemClipboard().hasContent(WalletRecord.DATA_FORMAT);
    }

    /// Retrieves the wallet record UUID from the system clipboard.
    ///
    /// @return the UUID stored in the clipboard, or `null` if no record UUID is present
    public UUID getClipboardUuid() {
        var clipboard = Clipboard.getSystemClipboard();
        var content = clipboard.getContent(WalletRecord.DATA_FORMAT);
        return content instanceof UUID uuid ? uuid : null;
    }

    /// Copies the specified record's UUID to the system clipboard.
    ///
    /// @param record the wallet record whose UUID should be copied
    public void putRecordToClipboard(WalletRecord record) {
        requireNonNull(record, "record must not be null");

        var clipboard = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.put(WalletRecord.DATA_FORMAT, record.uuid());
        clipboard.setContent(content);
    }
}
