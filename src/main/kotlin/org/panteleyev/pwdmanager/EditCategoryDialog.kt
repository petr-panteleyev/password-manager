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
import javafx.collections.FXCollections
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import org.controlsfx.validation.ValidationResult
import org.panteleyev.utilities.fx.BaseDialog

internal class EditCategoryDialog(category: Category) : BaseDialog<Category>(MainWindowController.CSS_PATH) {
    private val rb = PasswordManagerApplication.bundle

    private val nameEdit = TextField()
    private val typeList = ComboBox<RecordType>()
    private val pictureList = ComboBox<Picture>()

    init {
        title = rb.getString("categoryDialog.title")

        dialogPane.content = GridPane().apply {
            styleClass.add(Styles.GRID_PANE)

            addRow(0, Label(rb.getString("label.Name")), nameEdit)
            addRow(1, Label(rb.getString("label.defaultType")), typeList)
            addRow(2, Label(rb.getString("label.Icon")), pictureList)
        }

        nameEdit.prefColumnCount = 25

        createDefaultButtons(rb)

        initLists()

        nameEdit.text = category.name
        typeList.selectionModel.select(category.type)
        pictureList.selectionModel.select(category.picture)

        setResultConverter { b: ButtonType ->
            if (b == ButtonType.OK) {
                return@setResultConverter Category(
                        name = nameEdit.text,
                        type = typeList.selectionModel.selectedItem,
                        picture = pictureList.selectionModel.selectedItem
                )
            } else {
                return@setResultConverter null
            }
        }

        Platform.runLater {
            setupValidator()
            nameEdit.requestFocus()
        }
    }

    private fun initLists() {
        typeList.items = FXCollections.observableArrayList(*RecordType.values())
        typeList.setCellFactory { _ -> CardTypeListCell() }
        typeList.buttonCell = CardTypeListCell()
        Picture.setupComboBox(pictureList)
    }

    private fun setupValidator() {
        validation.registerValidator(nameEdit) { c: Control, _: String ->
            ValidationResult.fromErrorIf(c, null, nameEdit.text.isEmpty()) }
        validation.initInitialDecoration()
    }
}
