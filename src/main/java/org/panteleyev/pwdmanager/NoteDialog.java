/*
 * Copyright (c) 2016, 2018, Petr Panteleyev <petr@panteleyev.org>
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.validation.ValidationResult;
import org.panteleyev.utilities.fx.BaseDialog;
import java.util.ResourceBundle;

class NoteDialog extends BaseDialog<NewRecordDescriptor<Note>> implements Styles {
    private final ResourceBundle rb = PasswordManagerApplication.getBundle();

    private final TextField nameEdit = new TextField();
    private final CheckBox  createFromTop = new CheckBox(rb.getString("label.createFromRoot"));

    NoteDialog() {
        super(MainWindowController.CSS_PATH);

        setTitle(rb.getString("noteDialog.title"));

        var grid = new GridPane();
        grid.getStyleClass().add(GRID_PANE);

        grid.addRow(0, new Label(rb.getString("label.Name")), nameEdit);
        grid.addRow(1, createFromTop);

        GridPane.setColumnSpan(createFromTop, 2);

        getDialogPane().setContent(grid);
        createDefaultButtons(rb);

        nameEdit.setPrefColumnCount(20);

        setResultConverter(b -> b == ButtonType.OK ?
                new NewRecordDescriptor<>(createFromTop.isSelected(), new Note(nameEdit.getText(), "")) : null);

        Platform.runLater(this::setupValidator);
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
                ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
