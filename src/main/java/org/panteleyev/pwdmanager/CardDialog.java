/*
 * Copyright (c) 2016, 2020, Petr Panteleyev <petr@panteleyev.org>
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
import javafx.scene.control.ButtonType;
import org.panteleyev.pwdmanager.model.Card;
import java.util.Objects;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.RB;

class CardDialog extends RecordDialog {
    private final RecordType defaultType;
    private final Card card;

    CardDialog(RecordType defaultType, Card card) {
        Objects.requireNonNull(defaultType);

        this.defaultType = defaultType;
        this.card = card;

        initialize();
    }

    private void initialize() {
        setTitle(RB.getString("cardDialog.title"));
        createDefaultButtons(RB);

        initLists();
        setTypeLabelText(RB.getString("label.type"));

        if (card != null) {
            getNameEdit().setText(card.getName());
            getTypeList().getSelectionModel().select(card.getType());
            getPictureList().getSelectionModel().select(card.getPicture());
        } else {
            getNameEdit().setText("");
            getTypeList().getSelectionModel().select(defaultType);
            getPictureList().getSelectionModel().select(RecordType.PASSWORD.getPicture());
        }

        setResultConverter((ButtonType b) -> {
            if (b == ButtonType.OK) {
                var type = getTypeList().getSelectionModel().getSelectedItem();
                return Card.newCard(
                        getNameEdit().getText(),
                        getPictureList().getSelectionModel().getSelectedItem(),
                        type.getFieldSet()
                );
            } else {
                return null;
            }
        });

        Platform.runLater(this::setupValidator);
        Platform.runLater(getNameEdit()::requestFocus);
    }
}
