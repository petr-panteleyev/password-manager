/*
 * Copyright (c) 2017, Petr Panteleyev <petr@panteleyev.org>
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

package org.panteleyev.pwdmanager

import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import org.controlsfx.validation.ValidationResult
import org.panteleyev.utilities.fx.BaseDialog
import java.util.Objects

internal class EditNoteDialog(private val note: Note) : BaseDialog<Note>(null) {
    private val rb = PasswordManagerApplication.bundle

    private val nameEdit = TextField()
    private val noteEdit = TextArea()

    init {

        Objects.requireNonNull(note)

        initialize()
    }

    private fun initialize() {
        title = rb.getString("editNoteDialog.title")

        val box = HBox(5.0, Label(rb.getString("label.Name")), nameEdit).apply {
            alignment = Pos.CENTER_LEFT
            padding = Insets(0.0, 0.0, 5.0, 0.0)
        }
        nameEdit.prefColumnCount = 25

        val pane = BorderPane(noteEdit, box, null, null, null)
        BorderPane.setAlignment(noteEdit, Pos.CENTER)
        BorderPane.setAlignment(box, Pos.CENTER_LEFT)

        dialogPane.content = pane
        createDefaultButtons(rb)

        nameEdit.text = note.name
        noteEdit.text = note.text

        setResultConverter { b: ButtonType ->
            if (b == ButtonType.OK)
                Note(id = note.id, name = nameEdit.text, text = noteEdit.text)
            else
                null
        }

        Platform.runLater { this.setupValidator() }
        Platform.runLater { nameEdit.requestFocus() }
    }

    private fun createContextMenu(): ContextMenu {
        val menu = ContextMenu()

        val m1 = MenuItem("Copy...")
        m1.accelerator = KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN)
        m1.setOnAction { x ->

        }

        menu.items.addAll(m1)

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
        return menu
    }

    private fun setupValidator() {
        validation.registerValidator(nameEdit) { c: Control, _: String ->
            ValidationResult.fromErrorIf(c, null, nameEdit.text.isEmpty()) }
        validation.initInitialDecoration()
    }
}
