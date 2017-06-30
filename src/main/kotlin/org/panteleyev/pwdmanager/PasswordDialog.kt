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
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.layout.GridPane
import org.controlsfx.validation.ValidationResult
import org.panteleyev.utilities.fx.BaseDialog
import java.io.File
import java.util.UUID

internal class PasswordDialog(file: File, change: Boolean) : BaseDialog<String>(MainWindowController.CSS_PATH) {

    private val passwordEdit = PasswordField()
    private val passwordEdit2 = PasswordField()

    init {

        val rb = PasswordManagerApplication.bundle

        title = rb.getString("passwordDialog.title")

        val grid = GridPane()
        grid.styleClass.add(Styles.GRID_PANE)

        grid.addRow(0, Label(rb.getString("label.File")), Label(file.absolutePath))
        grid.addRow(1, Label(rb.getString("label.Password")), passwordEdit)
        if (change) {
            grid.addRow(2, Label(rb.getString("label.Repeat")), passwordEdit2)
        }
        passwordEdit.prefColumnCount = 32

        dialogPane.content = grid
        createDefaultButtons(rb)

        setResultConverter { b -> if (b == ButtonType.OK) passwordEdit.text else null }

        if (change) {
            Platform.runLater { this.createValidationSupport() }
        }
        Platform.runLater { passwordEdit.requestFocus() }
    }

    private fun createValidationSupport() {
        val v1 = { c: Control, _: String ->
            // Main password invalidates repeated password
            val s = passwordEdit2.text
            passwordEdit2.text = UUID.randomUUID().toString()
            passwordEdit2.text = s

            ValidationResult.fromErrorIf(c, null, false)
        }

        val v2 = { c: Control, _: String ->
            val equal = passwordEdit.text == passwordEdit2.text
            ValidationResult.fromErrorIf(c, null, !equal)
        }

        validation.registerValidator(passwordEdit, v1)
        validation.registerValidator(passwordEdit2, v2)
    }
}