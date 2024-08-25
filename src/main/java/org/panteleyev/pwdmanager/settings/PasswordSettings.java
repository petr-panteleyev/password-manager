/*
 Copyright © 2022-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.panteleyev.commons.xml.XMLEventReaderWrapper;
import org.panteleyev.commons.xml.XMLStreamWriterWrapper;
import org.panteleyev.pwdmanager.model.FieldType;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public class PasswordSettings {
    private static final QName ROOT_ELEMENT = new QName("passwords");
    private static final QName PASSWORD_ELEMENT = new QName("password");

    private static final QName TYPE_ATTR = new QName("fieldType");
    private static final QName UPPER_CASE_ATTR = new QName("upperCase");
    private static final QName LOWER_CASE_ATTR = new QName("lowerCase");
    private static final QName DIGITS_ATTR = new QName("digits");
    private static final QName SYMBOLS_ATTR = new QName("symbols");
    private static final QName LENGTH_ATTR = new QName("length");

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
        try (var w = XMLStreamWriterWrapper.newInstance(out)) {
            w.document(ROOT_ELEMENT, () -> {
                for (var entry : passwordOptions.entrySet()) {
                    w.element(PASSWORD_ELEMENT, Map.of(
                            TYPE_ATTR, entry.getKey(),
                            UPPER_CASE_ATTR, entry.getValue().upperCase(),
                            LOWER_CASE_ATTR, entry.getValue().lowerCase(),
                            DIGITS_ATTR, entry.getValue().digits(),
                            SYMBOLS_ATTR, entry.getValue().symbols(),
                            LENGTH_ATTR, entry.getValue().length()
                    ));
                }
            });
        }
    }

    void load(InputStream in) {
        passwordOptions.clear();

        try (var reader = XMLEventReaderWrapper.newInstance(in)) {
            while (reader.hasNext()) {
                var event = reader.nextEvent();
                event.ifStartElement(PASSWORD_ELEMENT, element -> passwordOptions.put(
                        element.getAttributeValue(TYPE_ATTR, FieldType.class).orElseThrow(),
                        new GeneratorOptions(
                                element.getAttributeValue(UPPER_CASE_ATTR, true),
                                element.getAttributeValue(LOWER_CASE_ATTR, true),
                                element.getAttributeValue(DIGITS_ATTR, true),
                                element.getAttributeValue(SYMBOLS_ATTR, true),
                                element.getAttributeValue(LENGTH_ATTR, 16)
                        )
                ));
            }
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
