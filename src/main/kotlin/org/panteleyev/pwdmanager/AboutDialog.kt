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

import javafx.scene.control.ButtonType
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import org.panteleyev.utilities.fx.BaseDialog
import java.util.ResourceBundle

internal class AboutDialog : BaseDialog<Any>(MainWindowController.CSS_PATH) {
    init {
        title = "About Password Manager"

        val rb = ResourceBundle.getBundle("BuildInfo")
        val l0 = Label("Password Manager").apply { styleClass.add(Styles.ABOUT_LABEL) }
        val l1 = Label("Copyright (c) 2016, 2017, Petr Panteleyev")

        val grid = GridPane().apply {
            styleClass.add(Styles.GRID_PANE)

            addRow(0, l0)
            addRow(1, l1)
            addRow(2, Label("Version:"), Label(rb.getString("version")))
            addRow(3, Label("Build:"), Label(rb.getString("timestamp")))
            addRow(4, Label("Encryption:"), Label("256-bit AES"))
        }

        GridPane.setColumnSpan(l0, 2)
        GridPane.setColumnSpan(l1, 2)

        dialogPane.content = grid
        dialogPane.buttonTypes.addAll(ButtonType.OK)
    }
}
