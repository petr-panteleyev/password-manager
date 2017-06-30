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
import javafx.scene.control.ButtonType
import javafx.scene.control.CheckBox
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import org.controlsfx.validation.ValidationResult
import org.panteleyev.utilities.fx.BaseDialog

internal class NoteDialog : BaseDialog<NewRecordDescriptor<Note>>(MainWindowController.CSS_PATH) {
    private val rb = PasswordManagerApplication.bundle

    private val nameEdit = TextField()
    private val createFromTop = CheckBox(rb.getString("label.createFromRoot"))

    init {
        title = rb.getString("noteDialog.title")

        val grid = GridPane()
        grid.styleClass.add(Styles.GRID_PANE)

        grid.addRow(0, Label(rb.getString("label.Name")), nameEdit)
        grid.addRow(1, createFromTop)

        GridPane.setColumnSpan(createFromTop, 2)

        dialogPane.content = grid
        createDefaultButtons(rb)

        nameEdit.prefColumnCount = 20

        setResultConverter { b ->
            if (b == ButtonType.OK)
                NewRecordDescriptor(createFromTop.isSelected, Note(name = nameEdit.text, text = ""))
            else
                null
        }

        Platform.runLater { this.setupValidator() }
    }

    private fun setupValidator() {
        validation.registerValidator(nameEdit) { c: Control, _: String ->
            ValidationResult.fromErrorIf(c, null, nameEdit.text.isEmpty()) }
        validation.initInitialDecoration()
    }
}
