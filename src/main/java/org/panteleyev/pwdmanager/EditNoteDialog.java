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

import java.util.Objects;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class EditNoteDialog extends Dialog<Note> {
    private final TextField noteNameEdit = new TextField();
    private final TextArea  noteEditor = new TextArea();

    public EditNoteDialog(Note note) {
        setTitle("Edit Note");
        initControls(note);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        BorderPane pane = new BorderPane();

        GridPane cardPropsPane = new GridPane();
        cardPropsPane.setHgap(5);
        cardPropsPane.setVgap(5);
        cardPropsPane.add(new Label("Name:"), 1, 1);
        cardPropsPane.add(noteNameEdit, 2, 1);

        pane.setTop(cardPropsPane);
        pane.setCenter(noteEditor);

        getDialogPane().setContent(pane);

        setResultConverter((ButtonType b) -> {
            return (b == ButtonType.OK) ?
                new Note(note.getId(), noteNameEdit.getText(), noteEditor.getText())
                : null;
        });

        Platform.runLater(() -> noteEditor.requestFocus());
    }

    private void initControls(Note note) {
        Objects.requireNonNull(note);

        noteNameEdit.setText(note.getName());
        noteEditor.setText(note.getText());
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
}
