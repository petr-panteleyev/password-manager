/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.panteleyev.pwdmanager.model.Card;

class RecordListCell extends ListCell<Card> {
    @Override
    protected void updateItem(Card record, boolean empty) {
        super.updateItem(record, empty);

        if (record == null || empty) {
            setText("");
            setGraphic(null);
        } else {
            if (record.favorite()) {
                getStyleClass().add("favorite");
            } else {
                getStyleClass().remove("favorite");
            }
            setText(record.name());
            setGraphic(new ImageView(record.picture().getImage()));
        }
    }
}
