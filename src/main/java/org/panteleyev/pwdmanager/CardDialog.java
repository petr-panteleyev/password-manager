/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import javafx.scene.control.ButtonType;
import org.panteleyev.pwdmanager.model.Card;
import java.util.Objects;
import static org.panteleyev.pwdmanager.Constants.RB;

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
            getNameEdit().setText(card.name());
            getTypeList().getSelectionModel().select(card.type());
            getPictureList().getSelectionModel().select(card.picture());
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
