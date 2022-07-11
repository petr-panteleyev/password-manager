/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import java.util.Optional;

public enum FontName {
    CONTROLS_FONT,
    MENU_FONT,
    CARD_CONTENT_FONT,
    CARD_TITLE_FONT,
    DIALOG_FONT;

    public static Optional<FontName> of(String str) {
        try {
            return Optional.of(valueOf(str));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
