/*
 Copyright © 2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.options;

import javafx.scene.text.Font;

public enum FontOption {
    CONTROLS_FONT,
    MENU_FONT,
    CARD_CONTENT_FONT,
    CARD_TITLE_FONT,
    DIALOG_FONT;

    private Font font;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }
}
