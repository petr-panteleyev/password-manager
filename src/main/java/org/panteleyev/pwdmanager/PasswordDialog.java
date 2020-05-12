package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;
import org.panteleyev.fx.BaseDialog;
import java.io.File;
import java.util.Objects;
import java.util.UUID;
import static org.panteleyev.fx.LabelFactory.newLabel;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.RB;

class PasswordDialog extends BaseDialog<String> implements Styles {
    private final PasswordField passwordEdit = new PasswordField();
    private final PasswordField passwordEdit2 = new PasswordField();

    PasswordDialog(File file, boolean change) {
        super(MainWindowController.CSS_PATH);

        setTitle(RB.getString("passwordDialog.title"));

        var grid = new GridPane();
        grid.getStyleClass().add(GRID_PANE);

        grid.addRow(0, newLabel(RB, "label.File"), new Label(file.getAbsolutePath()));
        grid.addRow(1, newLabel(RB, "label.Password"), passwordEdit);
        if (change) {
            grid.addRow(2, newLabel(RB, "label.Repeat"), passwordEdit2);
        }
        passwordEdit.setPrefColumnCount(32);

        getDialogPane().setContent(grid);
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
            boolean equal = Objects.equals(passwordEdit.getText(), passwordEdit2.getText());
            return ValidationResult.fromErrorIf(c, null, !equal);
        };

        validation.registerValidator(passwordEdit, v1);
        validation.registerValidator(passwordEdit2, v2);
    }
}
