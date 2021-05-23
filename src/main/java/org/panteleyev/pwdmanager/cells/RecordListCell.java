/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.panteleyev.pwdmanager.model.WalletRecord;
import static org.panteleyev.pwdmanager.Styles.STYLE_DELETED;
import static org.panteleyev.pwdmanager.Styles.STYLE_FAVORITE;

public class RecordListCell extends ListCell<WalletRecord> {
    @Override
    protected void updateItem(WalletRecord record, boolean empty) {
        super.updateItem(record, empty);

        getStyleClass().removeAll(STYLE_FAVORITE, STYLE_DELETED);
        if (record == null || empty) {
            setText("");
            setGraphic(null);
        } else {
            if (!record.active()) {
                getStyleClass().add(STYLE_DELETED);
            } else {
                if (record.favorite()) {
                    getStyleClass().add(STYLE_FAVORITE);
                }
            }
            setText(record.name());
            setGraphic(new ImageView(record.picture().getImage()));
        }
    }
}
