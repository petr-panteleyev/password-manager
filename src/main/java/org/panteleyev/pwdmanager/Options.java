/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.stage.Stage;
import org.panteleyev.generator.GeneratorOptions;
import org.panteleyev.pwdmanager.model.FieldType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.Preferences;

final class Options {
    private static final double DEFAULT_WIDTH = 800;
    private static final double DEFAULT_HEIGHT = 542;

    private static final String WINDOW_WIDTH_PREF = "window_width";
    private static final String WINDOW_HEIGHT_PREF = "window_height";
    private static final String PASSWORD_OPTIONS_PREF = "password_options";
    private static final String UPPER_CASE_PREF = "upper_case";
    private static final String LOWER_CASE_PREF = "lower_case";
    private static final String DIGITS_PREF = "digits";
    private static final String SYMBOLS_PREF = "symbols";
    private static final String LENGTH_PREF = "length";

    private Options() {
    }

    static final Map<FieldType, GeneratorOptions> PASSWORD_DEFAULTS = Map.ofEntries(
        Map.entry(FieldType.PIN,
            new GeneratorOptions(false, false, true, false, 4)),
        Map.entry(FieldType.UNIX_PASSWORD,
            new GeneratorOptions(true, true, true, true, 8)),
        Map.entry(FieldType.SHORT_PASSWORD,
            new GeneratorOptions(true, true, true, true, 16)),
        Map.entry(FieldType.LONG_PASSWORD,
            new GeneratorOptions(true, true, true, true, 32))
    );
    private static final Map<FieldType, GeneratorOptions> PASSWORD_OPTIONS = new HashMap<>(PASSWORD_DEFAULTS);

    private static final Preferences PREFS = Preferences.userNodeForPackage(PasswordManagerApplication.class);

    static void saveWindowDimensions(Stage stage) {
        PREFS.putDouble(WINDOW_WIDTH_PREF, stage.widthProperty().doubleValue());
        PREFS.putDouble(WINDOW_HEIGHT_PREF, stage.heightProperty().doubleValue());
    }

    static void loadWindowDimensions(Stage stage) {
        stage.setWidth(PREFS.getDouble(WINDOW_WIDTH_PREF, DEFAULT_WIDTH));
        stage.setHeight(PREFS.getDouble(WINDOW_HEIGHT_PREF, DEFAULT_HEIGHT));
    }

    public static void setPasswordOptions(Map<FieldType, GeneratorOptions> optionsMap) {
        PASSWORD_OPTIONS.clear();
        PASSWORD_OPTIONS.putAll(optionsMap);
    }

    public static Optional<GeneratorOptions> getPasswordOptions(FieldType fieldType) {
        var options = PASSWORD_OPTIONS.get(fieldType);
        if (options == null) {
            options = PASSWORD_DEFAULTS.get(fieldType);
        }
        return Optional.ofNullable(options);
    }

    static void saveOptions() {
        savePasswordOptions();
    }

    private static void savePasswordOptions() {
        PREFS.remove(PASSWORD_OPTIONS_PREF);
        var root = PREFS.node(PASSWORD_OPTIONS_PREF);

        for (var entry : PASSWORD_OPTIONS.entrySet()) {
            var entryNode = root.node(entry.getKey().name().toLowerCase());
            var options = entry.getValue();
            entryNode.putBoolean(UPPER_CASE_PREF, options.upperCase());
            entryNode.putBoolean(LOWER_CASE_PREF, options.lowerCase());
            entryNode.putBoolean(DIGITS_PREF, options.digits());
            entryNode.putBoolean(SYMBOLS_PREF, options.symbols());
            entryNode.putInt(LENGTH_PREF, options.length());
        }
    }

    static void loadPasswordOptions() {
        try {
            if (!PREFS.nodeExists(PASSWORD_OPTIONS_PREF)) {
                return;
            }

            var root = PREFS.node(PASSWORD_OPTIONS_PREF);

            for (var type : FieldType.values()) {
                var defaults = PASSWORD_DEFAULTS.get(type);
                if (defaults == null) {
                    continue;
                }

                var nodeName = type.name().toLowerCase();
                if (!root.nodeExists(nodeName)) {
                    continue;
                }
                var node = root.node(nodeName);

                PASSWORD_OPTIONS.put(type, new GeneratorOptions(
                    node.getBoolean(UPPER_CASE_PREF, defaults.upperCase()),
                    node.getBoolean(LOWER_CASE_PREF, defaults.lowerCase()),
                    node.getBoolean(DIGITS_PREF, defaults.digits()),
                    node.getBoolean(SYMBOLS_PREF, defaults.symbols()),
                    node.getInt(LENGTH_PREF, defaults.length())
                ));
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
