/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.panteleyev.pwdmanager.model.FieldType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordSettingsTest {
    @Test
    @DisplayName("should save and load password settings")
    public void testSaveLoad() throws IOException  {
        var options = Map.of(
                FieldType.LONG_PASSWORD, new GeneratorOptions(true, false, true, false, 10),
                FieldType.SHORT_PASSWORD, new GeneratorOptions(false, true, false, true, 15),
                FieldType.UNIX_PASSWORD, new GeneratorOptions(true, true, false, false, 8)
        );

        var settings = new PasswordSettings();
        settings.set(options);

        try (var out = new ByteArrayOutputStream()) {
            settings.save(out);

            try (var in = new ByteArrayInputStream(out.toByteArray())) {
                var loaded = new PasswordSettings();
                loaded.load(in);

                for (var entry: options.entrySet()) {
                    assertEquals(entry.getValue(), loaded.getPasswordOptions(entry.getKey()).orElse(null));
                }
            }
        }
    }
}
