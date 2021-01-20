/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
