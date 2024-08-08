/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.panteleyev.pwdmanager.settings.ColorName.ACTION_ADD;
import static org.panteleyev.pwdmanager.settings.ColorName.ACTION_DELETE;
import static org.panteleyev.pwdmanager.settings.ColorName.ACTION_REPLACE;
import static org.panteleyev.pwdmanager.settings.ColorName.ACTION_RESTORE;
import static org.panteleyev.pwdmanager.settings.ColorName.FIELD_NAME;
import static org.panteleyev.pwdmanager.settings.ColorName.FIELD_VALUE;
import static org.panteleyev.pwdmanager.settings.ColorName.HYPERLINK;

public class ColorSettingsTest {
    private static final Map<ColorName, Color> COLORS = Map.of(
            HYPERLINK, Color.RED,
            FIELD_NAME, Color.BLUEVIOLET,
            FIELD_VALUE, Color.WHITE,
            ACTION_ADD, Color.GREEN,
            ACTION_REPLACE, Color.YELLOW,
            ACTION_DELETE, Color.MAGENTA,
            ACTION_RESTORE, Color.BURLYWOOD
    );

    @Test
    @DisplayName("should save and load color settings")
    public void testSaveLoad() throws Exception {
        var settings = new ColorSettings();
        for (var entry : COLORS.entrySet()) {
            settings.setColor(entry.getKey(), entry.getValue());
        }

        try (var out = new ByteArrayOutputStream()) {
            settings.save(out);

            try (var in = new ByteArrayInputStream(out.toByteArray())) {
                var loaded = new ColorSettings();
                loaded.load(in);

                for (var entry: COLORS.entrySet()) {
                    assertEquals(entry.getValue(), loaded.getColor(entry.getKey()));
                }
            }
        }
    }
}
