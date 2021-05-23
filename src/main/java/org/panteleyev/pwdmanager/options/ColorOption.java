/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.options;

import javafx.scene.paint.Color;

public enum ColorOption {
    FAVORITE(Color.GREEN),
    FAVORITE_BACKGROUND(Color.WHITE),
    DELETED(Color.BLACK),
    DELETED_BACKGROUND(Color.PINK),
    HYPERLINK(Color.BLUE),
    FIELD_NAME(Color.BLUE),
    FIELD_VALUE(Color.BLACK),
    ACTION_ADD(Color.LIGHTGREEN),
    ACTION_REPLACE(Color.LIGHTYELLOW),
    ACTION_DELETE(Color.PINK),
    ACTION_RESTORE(Color.ORANGE);

    private Color color;

    ColorOption(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public String getWebString() {
        return "#"
            + colorToHex(color.getRed())
            + colorToHex(color.getGreen())
            + colorToHex(color.getBlue());
    }

    private static String colorToHex(double c) {
        var intValue = (int) (c * 255);
        var s = Integer.toString(intValue, 16);
        if (intValue < 16) {
            return "0" + s;
        } else {
            return s;
        }
    }
}
