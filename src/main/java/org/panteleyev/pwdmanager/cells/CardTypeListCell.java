/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.panteleyev.pwdmanager.model.RecordType;

public class CardTypeListCell extends ListCell<RecordType> {
    @Override
    public void updateItem(RecordType item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(item.getName());
            setGraphic(new ImageView(item.getImage()));
        }
    }
}
