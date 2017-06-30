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

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import org.panteleyev.utilities.fx.BaseDialog

private class EditableField(f: Field) {
    val typeProperty = SimpleObjectProperty<FieldType>()
    val nameProperty = SimpleStringProperty("name")
    val valueProperty = SimpleStringProperty("value")

    init {
        typeProperty.set(f.type)
        nameProperty.set(f.name)
        valueProperty.set(f.value)
    }
}

internal class EditCardDialog(private val card: Card) : BaseDialog<Card>(MainWindowController.CSS_PATH) {
    private val rb = PasswordManagerApplication.bundle

    private val cardContentView = TableView<EditableField>()
    private val fieldNameColumn = TableColumn<EditableField, String>()
    private val fieldValueColumn = TableColumn<EditableField, String>()
    private val fieldNameEdit = TextField()
    private val fieldTypeCombo = ComboBox<FieldType>()
    private val cardNameEdit = TextField()
    private val pictureList = ComboBox<Picture>()
    private val noteEditor = TextArea()

    private val selectedField: EditableField?
        get() = cardContentView.selectionModel.selectedItem

    init {
        initialize()
    }

    private fun initialize() {
        title = rb.getString("editCardDialog.title")

        val newFieldMenuItem = MenuItem(rb.getString("editCardDialog.menu.addField"))
        newFieldMenuItem.setOnAction { _ -> onNewField() }
        val deleteFieldMenuItem = MenuItem(rb.getString("editCardDialog.menu.deleteField"))
        deleteFieldMenuItem.setOnAction { _ -> onDeleteField() }

        val contextMenu = ContextMenu(newFieldMenuItem, SeparatorMenuItem(), deleteFieldMenuItem)

        with (fieldNameColumn) {
            isSortable = false
            style = "-fx-alignment: CENTER-RIGHT;"
            isSortable = false
            style = "-fx-alignment: CENTER-LEFT;"
        }
        cardContentView.columns.setAll(fieldNameColumn, fieldValueColumn)
        cardContentView.contextMenu = contextMenu
        cardContentView.isEditable = true

        val grid1 = GridPane()
        grid1.styleClass.add(Styles.GRID_PANE)
        grid1.addRow(0, Label(rb.getString("label.FieldName")), fieldNameEdit)
        grid1.addRow(1, Label(rb.getString("label.FieldType")), fieldTypeCombo)

        val pane = BorderPane(cardContentView, null, null, grid1, null)
        BorderPane.setAlignment(grid1, Pos.CENTER)
        BorderPane.setMargin(grid1, Insets(5.0, 0.0, 0.0, 0.0))

        val tab1 = Tab(rb.getString("editCardDialog.tab.fields"), pane)
        tab1.isClosable = false

        val tab2 = Tab(rb.getString("editCardDialog.tab.notes"), noteEditor)
        tab2.isClosable = false

        val grid3 = GridPane()
        grid3.styleClass.add(Styles.GRID_PANE)
        grid3.padding = Insets(5.0, 5.0, 5.0, 5.0)
        grid3.addRow(0, Label(rb.getString("label.Name")), cardNameEdit)
        grid3.addRow(1, Label(rb.getString("label.Icon")), pictureList)
        cardNameEdit.prefColumnCount = 30

        val tab3 = Tab(rb.getString("editCardDialog.tab.properties"), grid3)
        tab3.isClosable = false

        val tabPane = TabPane(tab1, tab2, tab3)

        dialogPane.content = tabPane
        createDefaultButtons(rb)

        fieldValueColumn.cellFactory = TextFieldTableCell.forTableColumn<EditableField>()

        fieldNameColumn.setCellValueFactory { p -> p.value.nameProperty }
        fieldValueColumn.setCellValueFactory { p -> p.value.valueProperty }

        fieldNameColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1))
        fieldValueColumn.prefWidthProperty().bind(cardContentView.widthProperty().divide(2).subtract(1))

        cardContentView.setItems(FXCollections.observableArrayList<EditableField>(card.fields.map { EditableField(it) }))

        cardContentView.selectionModel
                .selectedIndexProperty().addListener { _ -> onFieldSelected() }

        fieldNameEdit.setOnAction { _ -> onFieldNameChanged() }

        fieldTypeCombo.items = FXCollections.observableArrayList(*FieldType.values())
        fieldTypeCombo.setOnAction { _ -> onFieldTypeComboChanged() }

        noteEditor.text = card.note

        Picture.setupComboBox(pictureList)
        cardNameEdit.text = card.name
        pictureList.selectionModel.select(card.picture)

        setResultConverter { b: ButtonType ->
            if (b == ButtonType.OK) {
                return@setResultConverter Card(
                        id = card.id,
                        name = cardNameEdit.text,
                        picture = pictureList.selectionModel.selectedItem,
                        fields = cardContentView.items.map { Field(it.typeProperty.get(), it.nameProperty.get(), it.valueProperty.get())},
                        note = noteEditor.text)
            } else {
                return@setResultConverter null
            }
        }
    }

    private fun onFieldSelected() {
        selectedField?.let {
            fieldNameEdit.text = it.nameProperty.get()
            fieldTypeCombo.selectionModel.select(it.typeProperty.get())
        }
    }

    private fun onNewField() {
        val f = EditableField(Field(FieldType.STRING, "New field", ""))
        cardContentView.items.add(f)
        cardContentView.selectionModel.select(f)
    }

    private fun onDeleteField() {
        selectedField?.let {
            val alert = Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO)
            alert.showAndWait().filter { x -> x == ButtonType.YES }.ifPresent { _ -> cardContentView.items.remove(it) }
        }
    }

    private fun onFieldNameChanged() {
        selectedField?.let {
            val name = fieldNameEdit.text
            if (name != it.nameProperty.get()) {
                it.nameProperty.set(name)
            }
        }
    }

    private fun onFieldTypeComboChanged() {
        selectedField?.let {
            val type = fieldTypeCombo.value
            if (type != it.typeProperty.get()) {
                it.typeProperty.set(type)
            }
        }
    }
}
