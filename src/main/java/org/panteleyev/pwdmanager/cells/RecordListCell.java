/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ListCell;
import org.panteleyev.pwdmanager.model.WalletRecord;

import static org.panteleyev.pwdmanager.Styles.STYLE_DELETED;
import static org.panteleyev.pwdmanager.Styles.STYLE_FAVORITE;
import static org.panteleyev.pwdmanager.model.Picture.SMALL_IMAGE_SIZE;
import static org.panteleyev.pwdmanager.model.Picture.imageView;

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
            setGraphic(imageView(record.picture().getImage(), SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE));
        }
    }
}
