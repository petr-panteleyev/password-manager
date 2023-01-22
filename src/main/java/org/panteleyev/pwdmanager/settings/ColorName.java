/*
 Copyright © 2021-2023 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import javafx.scene.paint.Color;

import java.util.Optional;

public enum ColorName {
    HYPERLINK(Color.BLUE),
    FIELD_NAME(Color.BLUE),
    FIELD_VALUE(Color.BLACK),
    ACTION_ADD(Color.LIGHTGREEN),
    ACTION_REPLACE(Color.LIGHTYELLOW),
    ACTION_DELETE(Color.PINK),
    ACTION_RESTORE(Color.ORANGE);

    private final Color defaultColor;

    ColorName(Color defaultColor) {
        this.defaultColor = defaultColor;
    }

    public Color getDefaultColor() {
        return defaultColor;
    }

    public static Optional<ColorName> of(String str) {
        try {
            return Optional.of(valueOf(str));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}
