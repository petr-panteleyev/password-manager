// Copyright © 2017-2025 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Control;
import javafx.scene.control.PasswordField;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.functional.Scope.apply;
import static org.panteleyev.fx.factories.LabelFactory.label;
import static org.panteleyev.fx.factories.StringFactory.COLON;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.grid.GridPaneFactory.gridPane;
import static org.panteleyev.fx.factories.grid.GridRow.gridRow;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_FILE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSWORD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_REPEAT;

public final class PasswordDialog extends BaseDialog<String> {
    private final ValidationSupport validation = new ValidationSupport();

    private final PasswordField passwordEdit = new PasswordField();
    private final PasswordField passwordEdit2 = new PasswordField();

    public PasswordDialog(Controller owner, File file, boolean change) {
        super(owner, settings().getDialogCssFileUrl());

        setTitle(string(UI_BUNDLE, I18N_PASSWORD));

        passwordEdit.setPrefColumnCount(32);

        getDialogPane().setContent(apply(gridPane(
                List.of(
                        gridRow(label(string(UI_BUNDLE, I18N_FILE, COLON)), label(file.getAbsolutePath())),
                        gridRow(label(string(UI_BUNDLE, I18N_PASSWORD, COLON)), passwordEdit),
                        change ? gridRow(label(string(UI_BUNDLE, I18N_REPEAT, COLON)), passwordEdit2) : gridRow()
                )), pane -> pane.getStyleClass().add(STYLE_GRID_PANE)));

        createDefaultButtons(UI_BUNDLE);

        setResultConverter(buttonType -> OK.equals(buttonType) ? passwordEdit.getText() : null);

        if (change) {
            Platform.runLater(this::createValidationSupport);
        }
        Platform.runLater(() -> {
            passwordEdit.requestFocus();
            centerOnScreen();
        });
    }

    private void createValidationSupport() {
        Validator<String> v1 = (Control c, String _) -> {
            // Main password invalidates repeated password
            var s = passwordEdit2.getText();
            passwordEdit2.setText(UUID.randomUUID().toString());
            passwordEdit2.setText(s);

            return ValidationResult.fromErrorIf(c, null, false);
        };

        Validator<String> v2 = (Control c, String _) -> {
            var equal = Objects.equals(passwordEdit.getText(), passwordEdit2.getText());
            return ValidationResult.fromErrorIf(c, null, !equal);
        };

        validation.registerValidator(passwordEdit, v1);
        validation.registerValidator(passwordEdit2, v2);
    }
}
