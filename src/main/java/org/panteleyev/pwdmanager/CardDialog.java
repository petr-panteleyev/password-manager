/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.application.Platform;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.RecordType;
import java.util.Objects;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.pwdmanager.Constants.RB;

final class CardDialog extends RecordDialog {
    private final RecordType defaultType;

    CardDialog(RecordType defaultType) {
        Objects.requireNonNull(defaultType);

        this.defaultType = defaultType;

        initialize();
    }

    private void initialize() {
        setTitle(RB.getString("cardDialog.title"));
        createDefaultButtons(RB);

        initLists();
        setTypeLabelText(RB.getString("label.type"));

        getNameEdit().setText("");
        getTypeList().getSelectionModel().select(defaultType);
        getPictureList().getSelectionModel().select(RecordType.PASSWORD.getPicture());

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                var type = getTypeList().getSelectionModel().getSelectedItem();
                return new Card(
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
