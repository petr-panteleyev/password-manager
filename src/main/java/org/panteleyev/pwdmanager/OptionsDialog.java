/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import org.controlsfx.dialog.FontSelectorDialog;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.generator.GeneratorOptions;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.options.FontOption;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.BoxFactory.hBox;
import static org.panteleyev.fx.BoxFactory.vBox;
import static org.panteleyev.fx.ButtonFactory.button;
import static org.panteleyev.fx.FxFactory.newTab;
import static org.panteleyev.fx.FxFactory.textField;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.ELLIPSIS;
import static org.panteleyev.fx.FxUtils.SKIP;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.TitledPaneBuilder.titledPane;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.Options.PASSWORD_DEFAULTS;
import static org.panteleyev.pwdmanager.Options.options;
import static org.panteleyev.pwdmanager.Styles.BIG_SPACING;
import static org.panteleyev.pwdmanager.Styles.SMALL_SPACING;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_BACKGROUND;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COLORS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CONTROLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DELETED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DIALOGS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DIGITS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FAVORITE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_NAME;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_VALUE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FONTS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FOREGROUND;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LENGTH;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LOWER_CASE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_MENU;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_OPTIONS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSWORDS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SYMBOLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TEXT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UPPER_CASE;
import static org.panteleyev.pwdmanager.options.ColorOption.ACTION_ADD;
import static org.panteleyev.pwdmanager.options.ColorOption.ACTION_DELETE;
import static org.panteleyev.pwdmanager.options.ColorOption.ACTION_REPLACE;
import static org.panteleyev.pwdmanager.options.ColorOption.ACTION_RESTORE;
import static org.panteleyev.pwdmanager.options.ColorOption.DELETED;
import static org.panteleyev.pwdmanager.options.ColorOption.DELETED_BACKGROUND;
import static org.panteleyev.pwdmanager.options.ColorOption.FAVORITE;
import static org.panteleyev.pwdmanager.options.ColorOption.FAVORITE_BACKGROUND;
import static org.panteleyev.pwdmanager.options.ColorOption.FIELD_NAME;
import static org.panteleyev.pwdmanager.options.ColorOption.FIELD_VALUE;
import static org.panteleyev.pwdmanager.options.FontOption.CONTROLS_FONT;
import static org.panteleyev.pwdmanager.options.FontOption.DIALOG_FONT;
import static org.panteleyev.pwdmanager.options.FontOption.MENU_FONT;

final class OptionsDialog extends BaseDialog<ButtonType> {
    private final ComboBox<FieldType> typeComboBox = new ComboBox<>();

    private final CheckBox digitsCheckBox = new CheckBox(fxString(UI_BUNDLE, I18N_DIGITS));
    private final CheckBox upperCaseCheckBox = new CheckBox(fxString(UI_BUNDLE, I18N_UPPER_CASE));
    private final CheckBox lowerCaseCheckBox = new CheckBox(fxString(UI_BUNDLE, I18N_LOWER_CASE));
    private final CheckBox symbolsCheckBox = new CheckBox(fxString(UI_BUNDLE, I18N_SYMBOLS));
    private final ComboBox<Integer> lengthComboBox = new ComboBox<>();

    // Font text fields
    private final TextField controlsFontField = textField(20);
    private final TextField menuFontField = textField(20);
    private final TextField dialogFontField = textField(20);
    // Colors
    private final ColorPicker favoriteForegroundColorPicker = new ColorPicker(FAVORITE.getColor());
    private final ColorPicker favoriteBackgroundColorPicker = new ColorPicker(FAVORITE_BACKGROUND.getColor());
    private final ColorPicker deletedForegroundColorPicker = new ColorPicker(DELETED.getColor());
    private final ColorPicker deletedBackgroundColorPicker = new ColorPicker(DELETED_BACKGROUND.getColor());
    private final ColorPicker fieldNameColorPicker = new ColorPicker(FIELD_NAME.getColor());
    private final ColorPicker fieldValueColorPicker = new ColorPicker(FIELD_VALUE.getColor());
    private final ColorPicker actionAddColorPicker = new ColorPicker(ACTION_ADD.getColor());
    private final ColorPicker actionReplaceColorPicker = new ColorPicker(ACTION_REPLACE.getColor());
    private final ColorPicker actionDeleteColorPicker = new ColorPicker(ACTION_DELETE.getColor());
    private final ColorPicker actionRestoreColorPicker = new ColorPicker(ACTION_RESTORE.getColor());

    private final Map<FieldType, GeneratorOptions> passwordOptionsCopy = new EnumMap<>(FieldType.class);

    public OptionsDialog(Controller owner) {
        super(owner, options().getDialogCssFileUrl());
        setTitle(fxString(UI_BUNDLE, I18N_OPTIONS));

        controlsFontField.setEditable(false);
        menuFontField.setEditable(false);
        dialogFontField.setEditable(false);

        loadFont(CONTROLS_FONT, controlsFontField);
        loadFont(MENU_FONT, menuFontField);
        loadFont(FontOption.DIALOG_FONT, dialogFontField);

        createDefaultButtons(UI_BUNDLE, new ValidationSupport().invalidProperty());

        makePasswordOptionsLocalCopy();

        lengthComboBox.getItems().addAll(4, 6, 8, 16, 24, 32);
        typeComboBox.setItems(
            observableArrayList(
                PASSWORD_DEFAULTS.keySet().stream()
                    .sorted()
                    .toList()
            )
        );
        typeComboBox.getSelectionModel().selectedItemProperty().addListener(
            (observableValue, oldValue, newValue) -> updatePasswordControls(newValue));
        typeComboBox.getSelectionModel().selectFirst();

        EventHandler<ActionEvent> updatePasswordOptionsCopy = event -> onUpdatePasswordOptions();
        upperCaseCheckBox.setOnAction(updatePasswordOptionsCopy);
        lowerCaseCheckBox.setOnAction(updatePasswordOptionsCopy);
        digitsCheckBox.setOnAction(updatePasswordOptionsCopy);
        symbolsCheckBox.setOnAction(updatePasswordOptionsCopy);
        lengthComboBox.setOnAction(updatePasswordOptionsCopy);

        var vBox = vBox(BIG_SPACING,
            hBox(SMALL_SPACING, typeComboBox),
            hBox(SMALL_SPACING, upperCaseCheckBox, lowerCaseCheckBox, digitsCheckBox, symbolsCheckBox),
            hBox(SMALL_SPACING, label(fxString(UI_BUNDLE, I18N_LENGTH, COLON)), lengthComboBox)
        );
        vBox.setPadding(new Insets(BIG_SPACING, 0, BIG_SPACING, 0));

        getDialogPane().setContent(
            new TabPane(
                newTab(UI_BUNDLE, I18N_PASSWORDS, false, vBox),
                newTab(UI_BUNDLE, I18N_FONTS, false,
                    vBox(10,
                        titledPane(fxString(UI_BUNDLE, I18N_CONTROLS),
                            gridPane(List.of(
                                gridRow(label(fxString(UI_BUNDLE, I18N_TEXT, COLON)), controlsFontField,
                                    button(ELLIPSIS, actionEvent -> onFontSelected(controlsFontField))),
                                gridRow(label(fxString(UI_BUNDLE, I18N_MENU, COLON)), menuFontField,
                                    button(ELLIPSIS, actionEvent -> onFontSelected(menuFontField)))
                            ), b -> b.withStyle(STYLE_GRID_PANE))
                        ),
                        titledPane(fxString(UI_BUNDLE, I18N_DIALOGS),
                            gridPane(List.of(
                                gridRow(dialogFontField,
                                    button(ELLIPSIS, actionEvent -> onFontSelected(dialogFontField)))
                                ), b -> b.withStyle(STYLE_GRID_PANE)
                            )
                        )
                    )
                ),
                newTab(UI_BUNDLE, I18N_COLORS, false,
                    gridPane(List.of(
                        gridRow(SKIP,
                            label(fxString(UI_BUNDLE, I18N_FOREGROUND)), label(fxString(UI_BUNDLE, I18N_BACKGROUND))),
                        gridRow(label(fxString(UI_BUNDLE, I18N_FAVORITE, COLON)),
                            favoriteForegroundColorPicker, favoriteBackgroundColorPicker),
                        gridRow(label(fxString(UI_BUNDLE, I18N_DELETED, COLON)),
                            deletedForegroundColorPicker, deletedBackgroundColorPicker),
                        gridRow(label(fxString(UI_BUNDLE, I18N_FIELD_NAME, COLON)), fieldNameColorPicker),
                        gridRow(label(fxString(UI_BUNDLE, I18N_FIELD_VALUE, COLON)), fieldValueColorPicker),
                        gridRow(SKIP, label(fxString(UI_BUNDLE, I18N_IMPORT))),
                        gridRow(label(fxString(ImportAction.ADD.toString(), COLON)), actionAddColorPicker),
                        gridRow(label(fxString(ImportAction.REPLACE.toString(), COLON)), actionReplaceColorPicker),
                        gridRow(label(fxString(ImportAction.DELETE.toString(), COLON)), actionDeleteColorPicker),
                        gridRow(label(fxString(ImportAction.RESTORE.toString(), COLON)), actionRestoreColorPicker)
                    ), b -> b.withStyle(STYLE_GRID_PANE))
                )
            )
        );

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                Options.setPasswordOptions(passwordOptionsCopy);
                Options.saveOptions();

                // Fonts
                Options.setFont(CONTROLS_FONT, (Font) controlsFontField.getUserData());
                Options.setFont(MENU_FONT, (Font) menuFontField.getUserData());
                Options.setFont(DIALOG_FONT, (Font) dialogFontField.getUserData());

                // Colors
                Options.setColor(FAVORITE, favoriteForegroundColorPicker.getValue());
                Options.setColor(FAVORITE_BACKGROUND, favoriteBackgroundColorPicker.getValue());
                Options.setColor(DELETED, deletedForegroundColorPicker.getValue());
                Options.setColor(DELETED_BACKGROUND, deletedBackgroundColorPicker.getValue());
                Options.setColor(FIELD_NAME, fieldNameColorPicker.getValue());
                Options.setColor(FIELD_VALUE, fieldValueColorPicker.getValue());
                Options.setColor(ACTION_ADD, actionAddColorPicker.getValue());
                Options.setColor(ACTION_REPLACE, actionReplaceColorPicker.getValue());
                Options.setColor(ACTION_DELETE, actionDeleteColorPicker.getValue());
                Options.setColor(ACTION_RESTORE, actionRestoreColorPicker.getValue());

                options().generateCssFiles();
                options().reloadCssFile();
            }
            return buttonType;
        });
    }

    private void updatePasswordControls(FieldType type) {
        var options = passwordOptionsCopy.get(type);
        if (options == null) {
            throw new IllegalArgumentException(type.toString());
        }

        upperCaseCheckBox.setSelected(options.upperCase());
        lowerCaseCheckBox.setSelected(options.lowerCase());
        digitsCheckBox.setSelected(options.digits());
        symbolsCheckBox.setSelected(options.symbols());
        lengthComboBox.getSelectionModel().select((Integer) options.length());
    }

    private void makePasswordOptionsLocalCopy() {
        for (var type : FieldType.values()) {
            Options.getPasswordOptions(type).ifPresent(
                options -> passwordOptionsCopy.put(type, options)
            );
        }
    }

    private void onUpdatePasswordOptions() {
        var type = typeComboBox.getSelectionModel().getSelectedItem();
        if (type == null) {
            throw new IllegalStateException("Field type must be selected");
        }
        passwordOptionsCopy.put(type, buildOptions());
    }

    private GeneratorOptions buildOptions() {
        return new GeneratorOptions(
            upperCaseCheckBox.isSelected(),
            lowerCaseCheckBox.isSelected(),
            digitsCheckBox.isSelected(),
            symbolsCheckBox.isSelected(),
            lengthComboBox.getSelectionModel().getSelectedItem()
        );
    }

    private void onFontSelected(TextField field) {
        var font = (Font) field.getUserData();
        new FontSelectorDialog(font)
            .showAndWait()
            .ifPresent(newFont -> setupFontField(field, newFont));
    }

    private void loadFont(FontOption option, TextField field) {
        setupFontField(field, option.getFont());
    }

    private void setupFontField(TextField field, Font font) {
        field.setUserData(font);
        field.setText(String.format("%s %s, %d",
            font.getFamily(), font.getStyle(), (int) font.getSize()));
    }
}
