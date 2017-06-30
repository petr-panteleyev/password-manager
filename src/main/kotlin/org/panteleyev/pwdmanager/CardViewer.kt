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

import javafx.geometry.Pos
import javafx.scene.control.ContextMenu
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollPane
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException

private class FieldWrapper(val field: Field) {
    var show: Boolean = true

    val name : String
        get() = this.field.name
    val type: FieldType
        get() = this.field.type
    val value: String
        get() = this.field.value

    fun toggleShow() {
        show = !show
    }
}

internal class CardViewer : BorderPane() {
    private val rb = PasswordManagerApplication.bundle

    private val grid = GridPane()
    private val noteLabel = Label(rb.getString("label.notesNoSemicolon"), ImageView(Picture.NOTE.image))
    private val noteViewer = Label()

    init {
        grid.styleClass.add(Styles.GRID_PANE)
        grid.alignment = Pos.TOP_CENTER

        val vBox = VBox(
                grid,
                noteLabel,
                noteViewer
        )

        noteLabel.styleClass.add("noteLabel")
        noteViewer.styleClass.add("noteViewer")

        val pane = ScrollPane(vBox).apply {
            styleClass.add("whiteBackground")
            isFitToWidth = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        }
        center = pane

        BorderPane.setAlignment(pane, Pos.CENTER)
    }

    fun setData(items: List<Field>, note: String) {
        grid.children.clear()

        var y = 1
        for (field in items) {
            val nameLabel = Label(field.name)
            nameLabel.styleClass.add("fieldName")

            val valueLabel: Labeled
            if (field.type == FieldType.LINK) {
                valueLabel = Hyperlink(field.value)
                valueLabel.setOnAction { _ -> onHyperlinkClick(field.value) }
            } else {
                valueLabel = Label(if (field.type == FieldType.HIDDEN)
                    "***"
                else
                    field.value)

                valueLabel.setOnMouseClicked { event ->
                    if (event.clickCount > 1) {
                        onContentViewDoubleClick(FieldWrapper(field), valueLabel)
                    }
                }
            }

            valueLabel.contextMenu = createContextMenu(field)

            grid.add(nameLabel, 1, y)
            grid.add(valueLabel, 2, y++)
        }

        noteViewer.isVisible = !note.isEmpty()
        noteLabel.isVisible = !note.isEmpty()
        noteViewer.text = note
    }

    private fun onHyperlinkClick(url: String) {
        try {
            java.awt.Desktop.getDesktop().browse(URI(url))
        } catch (ex: URISyntaxException) {
            throw RuntimeException(ex)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }

    }

    private fun onContentViewDoubleClick(field: FieldWrapper, label: Labeled) {
        if (field.type == FieldType.HIDDEN) {
            field.toggleShow()
            label.text = if (field.show) field.value else "***"
        }
    }

    private fun createContextMenu(field: Field): ContextMenu {
        val copyMenuItem = MenuItem("Copy " + field.name)
        copyMenuItem.setOnAction { onCopy(field) }

        return ContextMenu(copyMenuItem)
    }

    private fun onCopy(field: Field) {
        val cb = Clipboard.getSystemClipboard()
        val content = ClipboardContent()

        var value = field.value
        if (field.type == FieldType.CREDIT_CARD_NUMBER) {
            // remove all spaces from credit card number
            value = value.trim { it <= ' ' }.replace(" ".toRegex(), "")
        }

        content.putString(value)
        cb.setContent(content)
    }
}
