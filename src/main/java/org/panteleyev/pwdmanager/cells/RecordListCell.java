/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.WalletRecord;

import static org.panteleyev.fx.BoxFactory.hBox;
import static org.panteleyev.pwdmanager.Styles.SMALL_SPACING;
import static org.panteleyev.pwdmanager.model.Picture.SMALL_IMAGE_SIZE;
import static org.panteleyev.pwdmanager.model.Picture.imageView;

public class RecordListCell extends ListCell<WalletRecord> {
    @Override
    protected void updateItem(WalletRecord record, boolean empty) {
        super.updateItem(record, empty);
        setText(null);
        if (record == null || empty) {
            setGraphic(null);
        } else {
            var label = new Label(record.name(),
                    imageView(record.picture().getImage(), SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE));

            Image rightImage = null;
            if (!record.active()) {
                rightImage = Picture.TRASH.getImage();
            } else if (record.favorite()) {
                rightImage = Picture.FAVORITES.getImage();
            }

            if (rightImage == null) {
                setGraphic(label);
            } else {
                label.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(label, Priority.ALWAYS);
                setGraphic(hBox(SMALL_SPACING,
                        label,
                        imageView(rightImage, SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE)
                ));
            }
        }
    }
}
