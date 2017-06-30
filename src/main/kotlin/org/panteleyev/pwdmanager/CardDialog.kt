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

internal class CardDialog(defaultType: RecordType, card: Card?) : RecordDialog<Card>() {

    init {
        title = rb.getString("cardDialog.title")
        createDefaultButtons(rb)

        initLists()
        setTypeLabelText(rb.getString("label.type"))

        if (card != null) {
            nameEdit.text = card.name
            typeList.selectionModel.select(card.type)
            pictureList.selectionModel.select(card.picture)
        } else {
            nameEdit.text = ""
            typeList.selectionModel.select(defaultType)
            pictureList.selectionModel.select(RecordType.PASSWORD.picture)
        }

        setResultConverter { b: ButtonType ->
            if (b == ButtonType.OK) {
                val type = typeList.selectionModel.selectedItem
                return@setResultConverter NewRecordDescriptor(isParentRoot, Card(
                        name = nameEdit.text,
                        picture = pictureList.selectionModel.selectedItem,
                        fields = type.fieldSet
                ))
            } else {
                return@setResultConverter null
            }
        }

        Platform.runLater {
            setupValidator()
            nameEdit.requestFocus()
        }
    }
}
