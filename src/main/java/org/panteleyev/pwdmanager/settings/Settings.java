/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.WindowManager;
import org.panteleyev.generator.GeneratorOptions;
import org.panteleyev.pwdmanager.ApplicationFiles;
import org.panteleyev.pwdmanager.TemplateEngine;
import org.panteleyev.pwdmanager.model.FieldType;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Map.entry;
import static javafx.application.Platform.runLater;
import static org.panteleyev.pwdmanager.GlobalContext.files;
import static org.panteleyev.pwdmanager.TemplateEngine.templateEngine;

public final class Settings {
    private final ApplicationFiles files;

    private final ColorSettings colorSettings = new ColorSettings();
    private final FontSettings fontSettings = new FontSettings();
    private final PasswordSettings passwordSettings = new PasswordSettings();
    private final WindowsSettings windowsSettings = new WindowsSettings();
    private final GeneralSettings generalSettings = new GeneralSettings();

    public Settings(ApplicationFiles files) {
        this.files = files;
    }

    public void update(Consumer<Settings> block) {
        block.accept(this);
        save();
        generateCssFiles();
        reloadCssFile();
    }

    public void generateCssFiles() {
        var dataModel = Map.ofEntries(
                entry("controlsFontFamily", fontSettings.getFont(FontName.CONTROLS_FONT).getFamily()),
                entry("controlsFontSize", (int) fontSettings.getFont(FontName.CONTROLS_FONT).getSize()),
                entry("menuFontFamily", fontSettings.getFont(FontName.MENU_FONT).getFamily()),
                entry("menuFontSize", (int) fontSettings.getFont(FontName.MENU_FONT).getSize()),
                // dialogs
                entry("dialogFontFamily", fontSettings.getFont(FontName.DIALOG_FONT).getFamily()),
                entry("dialogFontSize", (int) fontSettings.getFont(FontName.DIALOG_FONT).getSize()),
                entry("aboutLabelFontSize", (int) fontSettings.getFont(FontName.DIALOG_FONT).getSize() * 2),
                entry("aboutLabelSmallFontSize", (int) fontSettings.getFont(FontName.DIALOG_FONT).getSize() + 2),
                // Colors
                entry("favoriteColor", colorSettings.getWebString(ColorName.FAVORITE)),
                entry("favoriteBackground", colorSettings.getWebString(ColorName.FAVORITE_BACKGROUND)),
                entry("deletedColor", colorSettings.getWebString(ColorName.DELETED)),
                entry("deletedBackground", colorSettings.getWebString(ColorName.DELETED_BACKGROUND)),
                entry("fieldNameColor", colorSettings.getWebString(ColorName.FIELD_NAME)),
                entry("fieldValueColor", colorSettings.getWebString(ColorName.FIELD_VALUE)),
                entry("hyperLinkColor", colorSettings.getWebString(ColorName.HYPERLINK)),
                entry("actionAddColor", colorSettings.getWebString(ColorName.ACTION_ADD)),
                entry("actionReplaceColor", colorSettings.getWebString(ColorName.ACTION_REPLACE)),
                entry("actionDeleteColor", colorSettings.getWebString(ColorName.ACTION_DELETE)),
                entry("actionRestoreColor", colorSettings.getWebString(ColorName.ACTION_RESTORE))
        );

        files().write(ApplicationFiles.AppFile.MAIN_CSS, out -> templateEngine().process(
                TemplateEngine.Template.MAIN_CSS, dataModel, new OutputStreamWriter(out))
        );

        files().write(ApplicationFiles.AppFile.DIALOG_CSS, out -> templateEngine().process(
                TemplateEngine.Template.DIALOG_CSS, dataModel, new OutputStreamWriter(out)
        ));

        files().write(ApplicationFiles.AppFile.ABOUT_DIALOG_CSS, out -> templateEngine().process(
                TemplateEngine.Template.ABOUT_DIALOG_CSS, dataModel, new OutputStreamWriter(out)
        ));
    }

    public void reloadCssFile() {
        WindowManager.newInstance().getControllers().forEach(
                c -> runLater(() -> c.getStage().getScene().getStylesheets().setAll(getMainCssFilePath()))
        );
    }

    private void save() {
        files.write(ApplicationFiles.AppFile.COLORS, colorSettings::save);
        files.write(ApplicationFiles.AppFile.FONTS, fontSettings::save);
        files.write(ApplicationFiles.AppFile.PASSWORDS, passwordSettings::save);
        files.write(ApplicationFiles.AppFile.SETTINGS, generalSettings::save);
    }

    public void load() {
        files.read(ApplicationFiles.AppFile.COLORS, colorSettings::load);
        files.read(ApplicationFiles.AppFile.FONTS, fontSettings::load);
        files.read(ApplicationFiles.AppFile.PASSWORDS, passwordSettings::load);
        files.read(ApplicationFiles.AppFile.WINDOWS, windowsSettings::load);
        files.read(ApplicationFiles.AppFile.SETTINGS, generalSettings::load);
        generateCssFiles();
    }

    public String getMainCssFilePath() {
        return files.getUrl(ApplicationFiles.AppFile.MAIN_CSS).toExternalForm();
    }

    public URL getDialogCssFileUrl() {
        return files.getUrl(ApplicationFiles.AppFile.DIALOG_CSS);
    }

    public URL getAboutDialogCssFileUrl() {
        return files.getUrl(ApplicationFiles.AppFile.ABOUT_DIALOG_CSS);
    }

    public Color getColor(ColorName option) {
        return colorSettings.getColor(option);
    }

    public void setColor(ColorName option, Color color) {
        colorSettings.setColor(option, color);
    }

    public Font getFont(FontName option) {
        return fontSettings.getFont(option);
    }

    public void setFont(FontName option, Font font) {
        fontSettings.setFont(option, font);
    }

    public void setPasswordOptions(Map<FieldType, GeneratorOptions> map) {
        passwordSettings.set(map);
    }

    public Optional<GeneratorOptions> getPasswordOptions(FieldType fieldType) {
        return passwordSettings.getPasswordOptions(fieldType);
    }

    public Collection<FieldType> getPasswordFieldTypes() {
        return PasswordSettings.PASSWORD_DEFAULTS.keySet();
    }


    public void setCurrentFile(String currentFile) {
        generalSettings.put(GeneralSettings.Setting.CURRENT_FILE, currentFile);
        files.write(ApplicationFiles.AppFile.SETTINGS, generalSettings::save);
    }

    public String getCurrentFile() {
        return generalSettings.get(GeneralSettings.Setting.CURRENT_FILE);
    }

    public void saveStageDimensions(Controller controller) {
        windowsSettings.storeWindowDimensions(controller);
    }

    public void loadStageDimensions(Controller controller) {
        windowsSettings.restoreWindowDimensions(controller);
    }

    public void saveWindowsSettings() {
        files.write(ApplicationFiles.AppFile.WINDOWS, windowsSettings::save);
    }
}
