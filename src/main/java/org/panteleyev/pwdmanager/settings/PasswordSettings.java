/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.panteleyev.generator.GeneratorOptions;
import org.panteleyev.pwdmanager.model.FieldType;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.util.Map.entry;
import static org.panteleyev.pwdmanager.XMLUtils.appendElement;
import static org.panteleyev.pwdmanager.XMLUtils.createDocument;
import static org.panteleyev.pwdmanager.XMLUtils.readDocument;
import static org.panteleyev.pwdmanager.XMLUtils.writeDocument;

public class PasswordSettings {
    private static final String ROOT_ELEMENT = "passwords";
    private static final String PASSWORD_ELEMENT = "password";

    private static final String TYPE_ATTR = "fieldType";
    private static final String UPPER_CASE_ATTR = "upperCase";
    private static final String LOWER_CASE_ATTR = "lowerCase";
    private static final String DIGITS_ATTR = "digits";
    private static final String SYMBOLS_ATTR = "symbols";
    private static final String LENGTH_ATTR = "length";

    static final Map<FieldType, GeneratorOptions> PASSWORD_DEFAULTS = Map.ofEntries(
            entry(FieldType.PIN,
                    new GeneratorOptions(false, false, true, false, 4)),
            entry(FieldType.UNIX_PASSWORD,
                    new GeneratorOptions(true, true, true, true, 8)),
            entry(FieldType.SHORT_PASSWORD,
                    new GeneratorOptions(true, true, true, true, 16)),
            entry(FieldType.LONG_PASSWORD,
                    new GeneratorOptions(true, true, true, true, 32))
    );
    private final Map<FieldType, GeneratorOptions> passwordOptions = new HashMap<>(PASSWORD_DEFAULTS);

    void save(OutputStream out) {
        var root = createDocument(ROOT_ELEMENT);

        for (var entry : passwordOptions.entrySet()) {
            var e = appendElement(root, PASSWORD_ELEMENT);
            e.setAttribute(TYPE_ATTR, entry.getKey().name());
            e.setAttribute(UPPER_CASE_ATTR, Boolean.toString(entry.getValue().upperCase()));
            e.setAttribute(LOWER_CASE_ATTR, Boolean.toString(entry.getValue().lowerCase()));
            e.setAttribute(DIGITS_ATTR, Boolean.toString(entry.getValue().digits()));
            e.setAttribute(SYMBOLS_ATTR, Boolean.toString(entry.getValue().symbols()));
            e.setAttribute(LENGTH_ATTR, Integer.toString(entry.getValue().length()));
        }

        writeDocument(root.getOwnerDocument(), out);
    }

    void load(InputStream in) {
        passwordOptions.clear();
        var root = readDocument(in);
        var nodes = root.getElementsByTagName(PASSWORD_ELEMENT);
        for (int i = 0; i < nodes.getLength(); i++) {
            var e = (Element) nodes.item(i);
            var type = FieldType.valueOf(e.getAttribute(TYPE_ATTR));
            var upperCase = parseBoolean(e.getAttribute(UPPER_CASE_ATTR));
            var lowerCase = parseBoolean(e.getAttribute(LOWER_CASE_ATTR));
            var digits = parseBoolean(e.getAttribute(DIGITS_ATTR));
            var symbols = parseBoolean(e.getAttribute(SYMBOLS_ATTR));
            var length = parseInt(e.getAttribute(LENGTH_ATTR));
            passwordOptions.put(type, new GeneratorOptions(
                    upperCase, lowerCase, digits, symbols, length
            ));
        }
    }

    public Optional<GeneratorOptions> getPasswordOptions(FieldType fieldType) {
        var options = passwordOptions.get(fieldType);
        if (options == null) {
            options = PASSWORD_DEFAULTS.get(fieldType);
        }
        return Optional.ofNullable(options);
    }

    public void set(Map<FieldType, GeneratorOptions> map) {
        passwordOptions.clear();
        passwordOptions.putAll(map);
    }
}
