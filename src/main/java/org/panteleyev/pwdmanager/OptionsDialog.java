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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import org.controlsfx.validation.ValidationSupport;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.generator.GeneratorOptions;
import org.panteleyev.pwdmanager.model.FieldType;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;
import static javafx.collections.FXCollections.observableArrayList;
import static org.panteleyev.fx.BoxFactory.hBox;
import static org.panteleyev.fx.BoxFactory.vBox;
import static org.panteleyev.fx.FxFactory.newTab;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.pwdmanager.Constants.BIG_SPACING;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Constants.SMALL_SPACING;
import static org.panteleyev.pwdmanager.Options.PASSWORD_DEFAULTS;

class OptionsDialog extends BaseDialog<ButtonType> {
    private final ComboBox<FieldType> typeComboBox = new ComboBox<>();

    private final CheckBox digitsCheckBox = new CheckBox(fxString(RB, "Digits"));
    private final CheckBox upperCaseCheckBox = new CheckBox(fxString(RB, "Upper Case"));
    private final CheckBox lowerCaseCheckBox = new CheckBox(fxString(RB, "Lower Case"));
    private final CheckBox symbolsCheckBox = new CheckBox(fxString(RB, "Digits"));
    private final ComboBox<Integer> lengthComboBox = new ComboBox<>();

    private final Map<FieldType, GeneratorOptions> passwordOptionsCopy = new EnumMap<>(FieldType.class);

    public OptionsDialog(Controller owner) {
        super(owner, MainWindowController.CSS_PATH);
        setTitle(fxString(RB, "Options"));

        createDefaultButtons(RB, new ValidationSupport().invalidProperty());

        makePasswordOptionsLocalCopy();

        lengthComboBox.getItems().addAll(4, 6, 8, 16, 24, 32);
        typeComboBox.setItems(
            observableArrayList(PASSWORD_DEFAULTS.keySet().stream().sorted().collect(Collectors.toList()))
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
                newTab(RB, "Passwords", false, vBox)
            )
        );

        setResultConverter((ButtonType param) -> {
            if (param == ButtonType.OK) {
                Options.setPasswordOptions(passwordOptionsCopy);
                Options.saveOptions();
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
}
