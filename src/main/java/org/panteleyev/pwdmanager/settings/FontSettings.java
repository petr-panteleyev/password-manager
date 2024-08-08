/*
 Copyright © 2022-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.panteleyev.commons.xml.XMLEventReaderWrapper;
import org.panteleyev.commons.xml.XMLStreamWriterWrapper;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public final class FontSettings {
    private static final String DEFAULT_FONT_FAMILY = "System";
    private static final String DEFAULT_FONT_STYLE = "Normal Regular";
    private static final double DEFAULT_FONT_SIZE = 12;

    private static final QName ROOT_ELEMENT = new QName("fonts");
    private static final QName FONT_ELEMENT = new QName("font");
    private static final QName FONT_ATTR_NAME = new QName("name");
    private static final QName FONT_ATTR_FAMILY = new QName("family");
    private static final QName FONT_ATTR_STYLE = new QName("style");
    private static final QName FONT_ATTR_SIZE = new QName("size");

    private final Map<FontName, Font> fontMap = new ConcurrentHashMap<>();

    Font getFont(FontName fontName) {
        return fontMap.computeIfAbsent(fontName,
                _ -> Font.font(DEFAULT_FONT_FAMILY, FontWeight.NORMAL, FontPosture.REGULAR, DEFAULT_FONT_SIZE));
    }

    void setFont(FontName fontName, Font font) {
        fontMap.put(requireNonNull(fontName), requireNonNull(font));
    }

    void save(OutputStream out) {
        try (var w = XMLStreamWriterWrapper.newInstance(out)) {
            w.document(ROOT_ELEMENT, () -> {
                for (var entry : fontMap.entrySet()) {
                    var font = entry.getValue();
                    w.element(FONT_ELEMENT, Map.of(
                            FONT_ATTR_NAME, entry.getKey(),
                            FONT_ATTR_FAMILY, font.getFamily(),
                            FONT_ATTR_STYLE, font.getStyle(),
                            FONT_ATTR_SIZE, font.getSize()
                    ));
                }
            });
        }
    }

    void load(InputStream in) {
        fontMap.clear();

        try (var reader = XMLEventReaderWrapper.newInstance(in)) {
            while (reader.hasNext()) {
                var event = reader.nextEvent();
                event.ifStartElement(FONT_ELEMENT, element ->
                        FontName.of(element.getAttributeValue(FONT_ATTR_NAME, "").toUpperCase())
                                .ifPresent(option -> {
                                    var family = element.getAttributeValue(FONT_ATTR_FAMILY, DEFAULT_FONT_FAMILY);
                                    var style = element.getAttributeValue(FONT_ATTR_STYLE, DEFAULT_FONT_STYLE);
                                    var size = element.getAttributeValue(FONT_ATTR_SIZE, DEFAULT_FONT_SIZE);

                                    var font = Font.font(family,
                                            style.toLowerCase().contains("bold") ? FontWeight.BOLD : FontWeight.NORMAL,
                                            style.toLowerCase().contains("italic") ? FontPosture.ITALIC : FontPosture.REGULAR,
                                            size);

                                    fontMap.put(option, font);
                                }));
            }
        }
    }
}
