/*
 * Copyright (c) 2016, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

import java.io.File;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;

public class PasswordDialog extends Dialog<String> {
    private final PasswordField passwordEdit = new PasswordField();
    private final PasswordField passwordEdit2 = new PasswordField();

    public PasswordDialog(File file) {
        this(file, false);
    }

    public PasswordDialog(File file, boolean newPassword) {
        setTitle("Password");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);

        pane.add(new Label("File:"), 1, 1);
        pane.add(new Label(file.getAbsolutePath()), 2, 1);

        pane.add(new Label("Password:"), 1, 2);
        pane.add(passwordEdit, 2, 2);

        if (newPassword) {
            pane.add(new Label("Repeat:"), 1, 3);
            pane.add(passwordEdit2, 2, 3);

            final Button btOk = (Button) getDialogPane().lookupButton(ButtonType.OK);
            btOk.addEventFilter(ActionEvent.ACTION, event -> {
                if (!passwordEdit.getText().equals(passwordEdit2.getText())) {
                    event.consume();
                }
            });
        }

        passwordEdit.setPrefColumnCount(32);

        getDialogPane().setContent(pane);

        setResultConverter((ButtonType b) -> {
            return b == ButtonType.OK ? passwordEdit.getText() : null;
        });

        Platform.runLater(() -> passwordEdit.requestFocus());
    }
}
