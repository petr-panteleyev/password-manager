/*
 Copyright © 2022-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.pwdmanager.XMLUtils.appendObjectTextNode;
import static org.panteleyev.pwdmanager.XMLUtils.createDocument;
import static org.panteleyev.pwdmanager.XMLUtils.getStringNodeValue;
import static org.panteleyev.pwdmanager.XMLUtils.readDocument;
import static org.panteleyev.pwdmanager.XMLUtils.writeDocument;

final class GeneralSettings {
    private static final String ROOT = "settings";

    enum Setting {
        CURRENT_FILE("currentFile", "");

        private final String elementName;
        private final Object defaultValue;

        Setting(String elementName, Object defaultValue) {
            this.elementName = elementName;
            this.defaultValue = defaultValue;
        }

        public String getElementName() {
            return elementName;
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
        var root = createDocument(ROOT);
        for (var key : Setting.values()) {
            appendObjectTextNode(root, key.getElementName(), get(key));
        }
        writeDocument(root.getOwnerDocument(), out);
    }

    void load(InputStream in) {
        var rootElement = readDocument(in);

        for (var key : Setting.values()) {
            Optional<?> value = Optional.empty();
            if (key.getDefaultValue() instanceof String) {
                value = getStringNodeValue(rootElement, key.getElementName());
            }
            value.ifPresent(x -> settings.put(key, x));
        }
    }
}
