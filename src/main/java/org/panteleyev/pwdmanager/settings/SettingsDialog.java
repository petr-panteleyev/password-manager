// Copyright © 2020-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.settings;

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
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.ImportAction;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static javafx.collections.FXCollections.observableArrayList;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.Controller.SKIP;
import static org.panteleyev.fx.factories.BoxFactory.hBox;
import static org.panteleyev.fx.factories.BoxFactory.vBox;
import static org.panteleyev.fx.factories.ButtonFactory.button;
import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.ELLIPSIS;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.TabFactory.tab;
import static org.panteleyev.fx.factories.TextFieldFactory.textField;
import static org.panteleyev.fx.factories.TitledPaneFactory.titledPane;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Styles.BIG_SPACING;
import static org.panteleyev.pwdmanager.Styles.SMALL_SPACING;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COLORS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CONTROLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DIALOGS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_DIGITS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_NAME;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FIELD_VALUE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FONTS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LENGTH;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LOWER_CASE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_MENU;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_OPTIONS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSWORDS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SYMBOLS;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TEXT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UPPER_CASE;

public final class SettingsDialog extends BaseDialog<ButtonType> {
    private final ComboBox<FieldType> typeComboBox = new ComboBox<>();

    private final CheckBox digitsCheckBox = new CheckBox(string(UI_BUNDLE, I18N_DIGITS));
    private final CheckBox upperCaseCheckBox = new CheckBox(string(UI_BUNDLE, I18N_UPPER_CASE));
    private final CheckBox lowerCaseCheckBox = new CheckBox(string(UI_BUNDLE, I18N_LOWER_CASE));
    private final CheckBox symbolsCheckBox = new CheckBox(string(UI_BUNDLE, I18N_SYMBOLS));
    private final ComboBox<Integer> lengthComboBox = new ComboBox<>();

    // Font text fields
    private final TextField controlsFontField = textField(20);
    private final TextField menuFontField = textField(20);
    private final TextField dialogFontField = textField(20);
    // Colors
    private final ColorPicker fieldNameColorPicker = new ColorPicker(settings().getColor(ColorName.FIELD_NAME));
    private final ColorPicker fieldValueColorPicker = new ColorPicker(settings().getColor(ColorName.FIELD_VALUE));
    private final ColorPicker actionAddColorPicker = new ColorPicker(settings().getColor(ColorName.ACTION_ADD));
    private final ColorPicker actionReplaceColorPicker = new ColorPicker(settings().getColor(ColorName.ACTION_REPLACE));
    private final ColorPicker actionDeleteColorPicker = new ColorPicker(settings().getColor(ColorName.ACTION_DELETE));
    private final ColorPicker actionRestoreColorPicker = new ColorPicker(settings().getColor(ColorName.ACTION_RESTORE));

    private final Map<FieldType, GeneratorOptions> passwordOptionsCopy = new EnumMap<>(FieldType.class);

    public SettingsDialog(Controller owner) {
        super(owner, settings().getDialogCssFileUrl());
        setTitle(string(UI_BUNDLE, I18N_OPTIONS));

        controlsFontField.setEditable(false);
        menuFontField.setEditable(false);
        dialogFontField.setEditable(false);

        loadFont(FontName.CONTROLS_FONT, controlsFontField);
        loadFont(FontName.MENU_FONT, menuFontField);
        loadFont(FontName.DIALOG_FONT, dialogFontField);

        createDefaultButtons(UI_BUNDLE, new ValidationSupport().invalidProperty());

        makePasswordOptionsLocalCopy();

        lengthComboBox.getItems().addAll(4, 6, 8, 16, 24, 32);
        typeComboBox.setItems(observableArrayList(settings().getPasswordFieldTypes()));
        typeComboBox.getSelectionModel().selectedItemProperty().addListener(
                (_, _, newValue) -> updatePasswordControls(newValue));
        typeComboBox.getSelectionModel().selectFirst();

        EventHandler<ActionEvent> updatePasswordOptionsCopy = _ -> onUpdatePasswordOptions();
        upperCaseCheckBox.setOnAction(updatePasswordOptionsCopy);
        lowerCaseCheckBox.setOnAction(updatePasswordOptionsCopy);
        digitsCheckBox.setOnAction(updatePasswordOptionsCopy);
        symbolsCheckBox.setOnAction(updatePasswordOptionsCopy);
        lengthComboBox.setOnAction(updatePasswordOptionsCopy);

        var vBox = vBox(BIG_SPACING,
                hBox(SMALL_SPACING, typeComboBox),
                hBox(SMALL_SPACING, upperCaseCheckBox, lowerCaseCheckBox, digitsCheckBox, symbolsCheckBox),
                hBox(SMALL_SPACING, label(string(UI_BUNDLE, I18N_LENGTH, COLON)), lengthComboBox)
        );
        vBox.setPadding(new Insets(BIG_SPACING, 0, BIG_SPACING, 0));

        getDialogPane().setContent(
                new TabPane(
                        tab(string(UI_BUNDLE, I18N_PASSWORDS), vBox),
                        tab(string(UI_BUNDLE, I18N_FONTS),
                                vBox(10,
                                        titledPane(string(UI_BUNDLE, I18N_CONTROLS),
                                                gridPane(List.of(
                                                        gridRow(label(string(UI_BUNDLE, I18N_TEXT, COLON)),
                                                                controlsFontField,
                                                                button(ELLIPSIS,
                                                                        _ -> onFontSelected(controlsFontField))),
                                                        gridRow(label(string(UI_BUNDLE, I18N_MENU, COLON)),
                                                                menuFontField,
                                                                button(ELLIPSIS,
                                                                        _ -> onFontSelected(menuFontField)))
                                                ), null, List.of(STYLE_GRID_PANE))
                                        ),
                                        titledPane(string(UI_BUNDLE, I18N_DIALOGS),
                                                gridPane(List.of(
                                                        gridRow(dialogFontField,
                                                                button(ELLIPSIS,
                                                                        _ -> onFontSelected(dialogFontField)))
                                                ), null, List.of(STYLE_GRID_PANE))
                                        )
                                )
                        ),
                        tab(string(UI_BUNDLE, I18N_COLORS),
                                gridPane(List.of(
                                        gridRow(label(string(UI_BUNDLE, I18N_FIELD_NAME, COLON)),
                                                fieldNameColorPicker),
                                        gridRow(label(string(UI_BUNDLE, I18N_FIELD_VALUE, COLON)),
                                                fieldValueColorPicker),
                                        gridRow(SKIP, label(string(UI_BUNDLE, I18N_IMPORT))),
                                        gridRow(label(string(ImportAction.ADD.toString(), COLON)),
                                                actionAddColorPicker),
                                        gridRow(label(string(ImportAction.REPLACE.toString(), COLON)),
                                                actionReplaceColorPicker),
                                        gridRow(label(string(ImportAction.DELETE.toString(), COLON)),
                                                actionDeleteColorPicker),
                                        gridRow(label(string(ImportAction.RESTORE.toString(), COLON)),
                                                actionRestoreColorPicker)
                                ), null, List.of(STYLE_GRID_PANE))
                        )
                )
        );

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                settings().update(settings -> {
                    // Fonts
                    settings.setFont(FontName.CONTROLS_FONT, (Font) controlsFontField.getUserData());
                    settings.setFont(FontName.MENU_FONT, (Font) menuFontField.getUserData());
                    settings.setFont(FontName.DIALOG_FONT, (Font) dialogFontField.getUserData());

                    // Colors
                    settings.setColor(ColorName.FIELD_NAME, fieldNameColorPicker.getValue());
                    settings.setColor(ColorName.FIELD_VALUE, fieldValueColorPicker.getValue());
                    settings.setColor(ColorName.ACTION_ADD, actionAddColorPicker.getValue());
                    settings.setColor(ColorName.ACTION_REPLACE, actionReplaceColorPicker.getValue());
                    settings.setColor(ColorName.ACTION_DELETE, actionDeleteColorPicker.getValue());
                    settings.setColor(ColorName.ACTION_RESTORE, actionRestoreColorPicker.getValue());

                    settings.setPasswordOptions(passwordOptionsCopy);
                });

                settings().generateCssFiles();
                settings().reloadCssFile();
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
            settings().getPasswordOptions(type).ifPresent(
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

    private void loadFont(FontName option, TextField field) {
        setupFontField(field, settings().getFont(option));
    }

    private void setupFontField(TextField field, Font font) {
        field.setUserData(font);
        field.setText(String.format("%s %s, %d",
                font.getFamily(), font.getStyle(), (int) font.getSize()));
    }
}
