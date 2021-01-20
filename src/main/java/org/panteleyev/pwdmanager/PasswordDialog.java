/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
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
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Constants.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.Options.options;

class PasswordDialog extends BaseDialog<String> {
    private final ValidationSupport validation = new ValidationSupport();

    private final PasswordField passwordEdit = new PasswordField();
    private final PasswordField passwordEdit2 = new PasswordField();

    PasswordDialog(Controller owner, File file, boolean change) {
        super(owner, options().getDialogCssFileUrl());

        setTitle(RB.getString("passwordDialog.title"));

        passwordEdit.setPrefColumnCount(32);

        getDialogPane().setContent(gridPane(
            List.of(
                gridRow(label(fxString(RB, "label.File")), label(file.getAbsolutePath())),
                gridRow(label(fxString(RB, "label.Password")), passwordEdit),
                change ? gridRow(label(fxString(RB, "label.Repeat")), passwordEdit2) : gridRow()
            ), b -> b.withStyle(STYLE_GRID_PANE)
        ));

        createDefaultButtons(RB);

        setResultConverter(b -> b == ButtonType.OK ? passwordEdit.getText() : null);

        if (change) {
            Platform.runLater(this::createValidationSupport);
        }
        Platform.runLater(passwordEdit::requestFocus);
    }

    private void createValidationSupport() {
        Validator<String> v1 = (Control c, String value) -> {
            // Main password invalidates repeated password
            var s = passwordEdit2.getText();
            passwordEdit2.setText(UUID.randomUUID().toString());
            passwordEdit2.setText(s);

            return ValidationResult.fromErrorIf(c, null, false);
        };

        Validator<String> v2 = (Control c, String value) -> {
            var equal = Objects.equals(passwordEdit.getText(), passwordEdit2.getText());
            return ValidationResult.fromErrorIf(c, null, !equal);
        };

        validation.registerValidator(passwordEdit, v1);
        validation.registerValidator(passwordEdit2, v2);
    }
}
