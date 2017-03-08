/*
 * Copyright (c) 2016, 2017, Petr Panteleyev <petr@panteleyev.org>
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.controlsfx.validation.ValidationResult;
import org.panteleyev.utilities.fx.BaseDialog;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditNoteDialog extends BaseDialog<Note> implements Initializable {
    private static final String FXML_PATH = "/org/panteleyev/pwdmanager/EditNoteDialog.fxml";

    @FXML private TextField nameEdit;
    @FXML private TextArea noteEdit;

    private final Note note;

    EditNoteDialog(Note note) {
        super(FXML_PATH, MainWindowController.UI_BUNDLE_PATH);
        Objects.requireNonNull(note);

        this.note = note;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setTitle(resources.getString("editNoteDialog.title"));
        createDefaultButtons();

        nameEdit.setText(note.getName());
        noteEdit.setText(note.getText());

        setResultConverter((ButtonType b) -> (b == ButtonType.OK) ?
                new Note(note.getId(), nameEdit.getText(), noteEdit.getText())
                : null);

        Platform.runLater(this::setupValidator);
        Platform.runLater(nameEdit::requestFocus);
    }

    private ContextMenu createContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem m1 = new MenuItem("Copy...");
        m1.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        m1.setOnAction(x -> {

        });

        menu.getItems().addAll(m1);

        /*
        MenuItem m1 = new MenuItem("Add field...");
        m1.setAccelerator(new KeyCodeCombination(KeyCode.INSERT));
        m1.setOnAction(x -> {
            Field f = new Field(FieldType.STRING, "New field", "");
            cardContentView.getItems().add(f);
            cardContentView.getSelectionModel().select(f);
        });

        MenuItem m2 = new MenuItem("Delete Field");
        m2.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        m2.setOnAction(x -> onDeleteField());

        menu.getItems().addAll(m1, new SeparatorMenuItem(), m2);

*/
        return menu;
    }

    private void setupValidator() {
        validation.registerValidator(nameEdit, (Control c, String value) ->
                ValidationResult.fromErrorIf(c, null, nameEdit.getText().isEmpty()));
        validation.initInitialDecoration();
    }
}
