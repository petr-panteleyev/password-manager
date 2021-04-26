/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.panteleyev.fx.WindowManager;
import org.panteleyev.generator.GeneratorOptions;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.options.ColorOption;
import org.panteleyev.pwdmanager.options.FontOption;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.prefs.Preferences;
import static java.util.Map.entry;
import static javafx.application.Platform.runLater;
import static org.panteleyev.pwdmanager.TemplateEngine.templateEngine;

public final class Options {
    private enum Option {
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        PASSWORD_OPTIONS,
        UPPER_CASE,
        LOWER_CASE,
        DIGITS,
        SYMBOLS,
        LENGTH,
        FONTS,
        COLORS;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

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

    private static final String OPTIONS_DIRECTORY = ".password-manager";

    private static final String DEFAULT_FONT_FAMILY = "System";
    private static final String DEFAULT_FONT_STYLE = "Normal Regular";
    private static final double DEFAULT_FONT_SIZE = 12;

    private File mainCssFile;
    private File dialogCssFile;

    private static final Options OPTIONS = new Options();

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
    private static final Map<FieldType, GeneratorOptions> PASSWORD_OPTIONS = new HashMap<>(PASSWORD_DEFAULTS);

    private static final Preferences PREFS = Preferences.userNodeForPackage(PasswordManagerApplication.class);
    private static final Preferences FONT_PREFS = PREFS.node(Option.FONTS.toString());
    private static final Preferences COLOR_PREFS = PREFS.node(Option.COLORS.toString());

    public static Options options() {
        return OPTIONS;
    }

    private Options() {
    }

    public void initialize() {
        var settingsDirectory = initDirectory(
            new File(System.getProperty("user.home") + File.separator + OPTIONS_DIRECTORY),
            "Options"
        );

        initDirectory(
            new File(settingsDirectory, "logs"),
            "Logs"
        );

        mainCssFile = new File(settingsDirectory, "main.css");
        dialogCssFile = new File(settingsDirectory, "dialog.css");
    }

    private static File initDirectory(File dir, String name) {
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException(name + " directory cannot be opened/created");
            }
        } else {
            if (!dir.isDirectory()) {
                throw new RuntimeException(name + " directory cannot be opened/created");
            }
        }
        return dir;
    }

    public void loadFontOptions() {
        for (var option : FontOption.values()) {
            var prefs = FONT_PREFS.node(option.toString());

            var style = prefs.get("style", DEFAULT_FONT_STYLE);
            var font = Font.font(prefs.get("family", DEFAULT_FONT_FAMILY),
                style.toLowerCase().contains("bold") ? FontWeight.BOLD : FontWeight.NORMAL,
                style.toLowerCase().contains("italic") ? FontPosture.ITALIC : FontPosture.REGULAR,
                prefs.getDouble("size", DEFAULT_FONT_SIZE));

            option.setFont(font);
        }
    }

    public void loadColorOptions() {
        for (var option : ColorOption.values()) {
            var colorString = COLOR_PREFS.get(option.toString(), null);
            if (colorString != null) {
                option.setColor(Color.valueOf(colorString));
            }
        }
    }

    public void generateCssFiles() {
        var dataModel = Map.ofEntries(
            entry("controlsFontFamily", FontOption.CONTROLS_FONT.getFont().getFamily()),
            entry("controlsFontSize", (int) FontOption.CONTROLS_FONT.getFont().getSize()),
            entry("menuFontFamily", FontOption.MENU_FONT.getFont().getFamily()),
            entry("menuFontSize", (int) FontOption.MENU_FONT.getFont().getSize()),
            // dialogs
            entry("dialogFontFamily", FontOption.DIALOG_FONT.getFont().getFamily()),
            entry("dialogFontSize", (int) FontOption.DIALOG_FONT.getFont().getSize()),
            entry("aboutLabelFontSize", (int) FontOption.DIALOG_FONT.getFont().getSize() + 4),
            // Colors
            entry("favoriteColor", ColorOption.FAVORITE.getWebString()),
            entry("favoriteBackground", ColorOption.FAVORITE_BACKGROUND.getWebString()),
            entry("fieldNameColor", ColorOption.FIELD_NAME.getWebString()),
            entry("fieldValueColor", ColorOption.FIELD_VALUE.getWebString()),
            entry("hyperLinkColor", ColorOption.HYPERLINK.getWebString()),
            entry("actionAddColor", ColorOption.ACTION_ADD.getWebString()),
            entry("actionReplaceColor", ColorOption.ACTION_REPLACE.getWebString())
        );

        try (var w = new FileWriter(mainCssFile)) {
            templateEngine().process(TemplateEngine.Template.MAIN_CSS, dataModel, w);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

        try (var w = new FileWriter(dialogCssFile)) {
            templateEngine().process(TemplateEngine.Template.DIALOG_CSS, dataModel, w);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public void reloadCssFile() {
        WindowManager.newInstance().getControllers().forEach(
            c -> runLater(() -> c.getStage().getScene().getStylesheets().setAll(getMainCssFilePath()))
        );
    }

    public String getMainCssFilePath() {
        try {
            return mainCssFile.toURI().toURL().toExternalForm();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public URL getDialogCssFileUrl() {
        try {
            return dialogCssFile.toURI().toURL();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

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

    public static void setFont(FontOption option, Font font) {
        if (font == null) {
            return;
        }

        var prefs = FONT_PREFS.node(option.toString());
        prefs.put("family", font.getFamily());
        prefs.put("style", font.getStyle());
        prefs.putDouble("size", font.getSize());

        option.setFont(font);
    }

    public static void setColor(ColorOption option, Color color) {
        if (color == null) {
            return;
        }

        COLOR_PREFS.put(option.toString(), color.toString());
        option.setColor(color);
    }
}
