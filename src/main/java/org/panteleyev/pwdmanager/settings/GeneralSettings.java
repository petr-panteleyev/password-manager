/*
 Copyright © 2022-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.panteleyev.commons.xml.XMLEventReaderWrapper;
import org.panteleyev.commons.xml.XMLStreamWriterWrapper;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

final class GeneralSettings {
    private static final QName ROOT = new QName("settings");

    enum Setting {
        CURRENT_FILE("currentFile", "");

        private final String elementName;
        private final Object defaultValue;

        Setting(String elementName, Object defaultValue) {
            this.elementName = elementName;
            this.defaultValue = defaultValue;
        }

        public QName getElementName() {
            return new QName(elementName);
        }

        public Object getDefaultValue() {
            return defaultValue;
        }
    }

    private final Map<Setting, Object> settings = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    <T> T get(Setting key) {
        return (T) settings.computeIfAbsent(key, _ -> key.getDefaultValue());
    }

    void put(Setting key, Object value) {
        settings.put(key, requireNonNull(value));
    }

    void save(OutputStream out) {
        try (var w = XMLStreamWriterWrapper.newInstance(out)) {
            w.document(ROOT, () -> {
                for (var key : Setting.values()) {
                    w.textElement(key.getElementName(), get(key).toString());
                }
            });
        }
    }

    void load(InputStream in) {
        try (var reader = XMLEventReaderWrapper.newInstance(in)) {
            while (reader.hasNext()) {
                var event = reader.nextEvent();

                for (var key : Setting.values()) {
                    event.ifStartElement(key.getElementName(), _ -> {
                        Optional<?> value = Optional.empty();
                        if (key.getDefaultValue() instanceof String) {
                            value = reader.getElementText();
                        }
                        value.ifPresent(x -> settings.put(key, x));
                    });
                }
            }
        }
    }
}
