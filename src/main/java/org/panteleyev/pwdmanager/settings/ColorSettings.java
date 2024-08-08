/*
 Copyright © 2021-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import javafx.scene.paint.Color;
import org.panteleyev.commons.xml.XMLEventReaderWrapper;
import org.panteleyev.commons.xml.XMLStreamWriterWrapper;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

final class ColorSettings {
    private static final QName ROOT_ELEMENT = new QName("colors");
    private static final QName COLOR_ELEMENT = new QName("color");
    private static final QName COLOR_ATTR_NAME = new QName("name");
    private static final QName COLOR_ATTR_VALUE = new QName("value");

    private final Map<ColorName, Color> colorMap = new ConcurrentHashMap<>();

    Color getColor(ColorName option) {
        return colorMap.computeIfAbsent(option, _ -> option.getDefaultColor());
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
        try (var w = XMLStreamWriterWrapper.newInstance(out)) {
            w.document(ROOT_ELEMENT, () -> {
                for (var opt : ColorName.values()) {
                    w.element(COLOR_ELEMENT, Map.of(
                            COLOR_ATTR_NAME, opt,
                            COLOR_ATTR_VALUE, getWebString(opt)
                    ));
                }
            });
        }
    }

    void load(InputStream in) {
        colorMap.clear();
        try (var reader = XMLEventReaderWrapper.newInstance(in)) {
            while (reader.hasNext()) {
                var event = reader.nextEvent();
                event.ifStartElement(COLOR_ELEMENT, element ->
                        ColorName.of(element.getAttributeValue(COLOR_ATTR_NAME, "").toUpperCase())
                                .ifPresent(option -> colorMap.put(
                                                option,
                                                Color.valueOf(element.getAttributeValue(COLOR_ATTR_VALUE, ""))
                                        )
                                )
                );
            }
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
