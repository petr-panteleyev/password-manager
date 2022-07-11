/*
 Copyright © 2021-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import javafx.scene.paint.Color;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.pwdmanager.XMLUtils.appendElement;
import static org.panteleyev.pwdmanager.XMLUtils.createDocument;
import static org.panteleyev.pwdmanager.XMLUtils.readDocument;
import static org.panteleyev.pwdmanager.XMLUtils.writeDocument;

final class ColorSettings {
    private static final String ROOT_ELEMENT = "colors";
    private static final String COLOR_ELEMENT = "color";
    private static final String COLOR_ATTR_NAME = "name";
    private static final String COLOR_ATTR_VALUE = "value";

    private final Map<ColorName, Color> colorMap = new ConcurrentHashMap<>();

    Color getColor(ColorName option) {
        return colorMap.computeIfAbsent(option, key -> option.getDefaultColor());
    }

    String getWebString(ColorName option) {
        var color = getColor(option);
        return "#"
                + colorToHex(color.getRed())
                + colorToHex(color.getGreen())
                + colorToHex(color.getBlue());
    }

    void setColor(ColorName option, Color color) {
        colorMap.put(
                requireNonNull(option),
                requireNonNull(color)
        );
    }

    void save(OutputStream out) {
        var root = createDocument(ROOT_ELEMENT);

        for (var opt : ColorName.values()) {
            var e = appendElement(root, COLOR_ELEMENT);
            e.setAttribute(COLOR_ATTR_NAME, opt.name());
            e.setAttribute(COLOR_ATTR_VALUE, getWebString(opt));
        }

        writeDocument(root.getOwnerDocument(), out);
    }

    void load(InputStream in) {
        colorMap.clear();
        var root = readDocument(in);
        var colorNodes = root.getElementsByTagName(COLOR_ELEMENT);
        for (int i = 0; i < colorNodes.getLength(); i++) {
            var colorElement = (Element) colorNodes.item(i);
            ColorName.of(colorElement.getAttribute(COLOR_ATTR_NAME).toUpperCase())
                    .ifPresent(option -> colorMap.put(option,
                            Color.valueOf(colorElement.getAttribute(COLOR_ATTR_VALUE))));
        }
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
