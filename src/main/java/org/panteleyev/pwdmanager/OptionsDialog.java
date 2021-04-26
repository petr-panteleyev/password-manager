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
import org.panteleyev.pwdmanager.options.FontOption;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import static javafx.collections.FXCollections.observableArrayList;
import static org.panteleyev.fx.BoxFactory.hBox;
import static org.panteleyev.fx.BoxFactory.vBox;
import static org.panteleyev.fx.ButtonFactory.button;
import static org.panteleyev.fx.FxFactory.newTab;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.ELLIPSIS;
import static org.panteleyev.fx.FxUtils.SKIP;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.TitledPaneBuilder.titledPane;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Options.PASSWORD_DEFAULTS;
import static org.panteleyev.pwdmanager.Options.options;
import static org.panteleyev.pwdmanager.Styles.BIG_SPACING;
import static org.panteleyev.pwdmanager.Styles.SMALL_SPACING;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.options.ColorOption.ACTION_ADD;
import static org.panteleyev.pwdmanager.options.ColorOption.ACTION_REPLACE;
import static org.panteleyev.pwdmanager.options.ColorOption.FAVORITE;
import static org.panteleyev.pwdmanager.options.ColorOption.FAVORITE_BACKGROUND;
import static org.panteleyev.pwdmanager.options.ColorOption.FIELD_NAME;
import static org.panteleyev.pwdmanager.options.ColorOption.FIELD_VALUE;
import static org.panteleyev.pwdmanager.options.FontOption.CONTROLS_FONT;
import static org.panteleyev.pwdmanager.options.FontOption.DIALOG_FONT;
import static org.panteleyev.pwdmanager.options.FontOption.MENU_FONT;

class OptionsDialog extends BaseDialog<ButtonType> {
    private final ComboBox<FieldType> typeComboBox = new ComboBox<>();

    private final CheckBox digitsCheckBox = new CheckBox(fxString(RB, "Digits"));
    private final CheckBox upperCaseCheckBox = new CheckBox(fxString(RB, "Upper Case"));
    private final CheckBox lowerCaseCheckBox = new CheckBox(fxString(RB, "Lower Case"));
    private final CheckBox symbolsCheckBox = new CheckBox(fxString(RB, "Digits"));
    private final ComboBox<Integer> lengthComboBox = new ComboBox<>();

    // Font text fields
    private final TextField controlsFontField = new TextField();
    private final TextField menuFontField = new TextField();
    private final TextField dialogFontField = new TextField();
    // Colors
    private final ColorPicker favoriteForegroundColorPicker = new ColorPicker(FAVORITE.getColor());
    private final ColorPicker favoriteBackgroundColorPicker = new ColorPicker(FAVORITE_BACKGROUND.getColor());
    private final ColorPicker fieldNameColorPicker = new ColorPicker(FIELD_NAME.getColor());
    private final ColorPicker fieldValueColorPicker = new ColorPicker(FIELD_VALUE.getColor());
    private final ColorPicker actionAddColorPicker = new ColorPicker(ACTION_ADD.getColor());
    private final ColorPicker actionReplaceColorPicker = new ColorPicker(ACTION_REPLACE.getColor());

    private final Map<FieldType, GeneratorOptions> passwordOptionsCopy = new EnumMap<>(FieldType.class);

    public OptionsDialog(Controller owner) {
        super(owner, options().getDialogCssFileUrl());
        setTitle(fxString(RB, "Options"));

        controlsFontField.setEditable(false);
        controlsFontField.setPrefColumnCount(20);
        menuFontField.setEditable(false);
        menuFontField.setPrefColumnCount(20);
        dialogFontField.setEditable(false);
        dialogFontField.setPrefColumnCount(20);

        loadFont(CONTROLS_FONT, controlsFontField);
        loadFont(MENU_FONT, menuFontField);
        loadFont(FontOption.DIALOG_FONT, dialogFontField);

        createDefaultButtons(RB, new ValidationSupport().invalidProperty());

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
            hBox(SMALL_SPACING, label(fxString(RB, "Length", ":")), lengthComboBox)
        );
        vBox.setPadding(new Insets(BIG_SPACING, 0, BIG_SPACING, 0));

        getDialogPane().setContent(
            new TabPane(
                newTab(RB, "Passwords", false, vBox),
                newTab(RB, "Fonts", false,
                    vBox(10,
                        titledPane(fxString(RB, "Controls"),
                            gridPane(List.of(
                                gridRow(label(fxString(RB, "Text", COLON)), controlsFontField,
                                    button(ELLIPSIS, actionEvent -> onFontSelected(controlsFontField))),
                                gridRow(label(fxString(RB, "Menu", COLON)), menuFontField,
                                    button(ELLIPSIS, actionEvent -> onFontSelected(menuFontField)))
                            ), b -> b.withStyle(STYLE_GRID_PANE))
                        ),
                        titledPane(fxString(RB, "Dialogs"),
                            gridPane(List.of(
                                gridRow(dialogFontField,
                                    button(ELLIPSIS, actionEvent -> onFontSelected(dialogFontField)))
                                ), b -> b.withStyle(STYLE_GRID_PANE)
                            )
                        )
                    )
                ),
                newTab(RB, "Colors", false,
                    gridPane(List.of(
                        gridRow(SKIP, label(fxString(RB, "Foreground")), label(fxString(RB, "Background"))),
                        gridRow(label(fxString(RB, "Favorite", COLON)),
                            favoriteForegroundColorPicker, favoriteBackgroundColorPicker),
                        gridRow(label(fxString(RB, "Field_Name", COLON)), fieldNameColorPicker),
                        gridRow(label(fxString(RB, "Field_Value", COLON)), fieldValueColorPicker),
                        gridRow(SKIP, label(fxString(RB, "Import"))),
                        gridRow(label(fxString(RB, "Add", COLON)), actionAddColorPicker),
                        gridRow(label(fxString(RB, "Replace", COLON)), actionReplaceColorPicker)
                    ), b -> b.withStyle(STYLE_GRID_PANE))
                )
            )
        );

        setResultConverter((ButtonType param) -> {
            if (param == ButtonType.OK) {
                Options.setPasswordOptions(passwordOptionsCopy);
                Options.saveOptions();

                // Fonts
                Options.setFont(CONTROLS_FONT, (Font) controlsFontField.getUserData());
                Options.setFont(MENU_FONT, (Font) menuFontField.getUserData());
                Options.setFont(DIALOG_FONT, (Font) dialogFontField.getUserData());

                // Colors
                Options.setColor(FAVORITE, favoriteForegroundColorPicker.getValue());
                Options.setColor(FAVORITE_BACKGROUND, favoriteBackgroundColorPicker.getValue());
                Options.setColor(FIELD_NAME, fieldNameColorPicker.getValue());
                Options.setColor(FIELD_VALUE, fieldValueColorPicker.getValue());
                Options.setColor(ACTION_ADD, actionAddColorPicker.getValue());
                Options.setColor(ACTION_REPLACE, actionReplaceColorPicker.getValue());

                options().generateCssFiles();
                options().reloadCssFile();
            }
            return param;
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
            Options.getPasswordOptions(type).ifPresent(options -> passwordOptionsCopy.put(type, options));
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
