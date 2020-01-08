/*
 * Copyright (c) 2016, 2020, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;
import org.panteleyev.commons.fx.BaseDialog;
import java.io.File;
import java.util.Objects;
import java.util.UUID;
import static org.panteleyev.commons.fx.FXFactory.newLabel;
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
