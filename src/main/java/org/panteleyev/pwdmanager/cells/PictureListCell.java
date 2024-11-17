/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ListCell;
import org.panteleyev.pwdmanager.model.Picture;

import static org.panteleyev.pwdmanager.model.Picture.imageView;

public class PictureListCell extends ListCell<Picture> {
    @Override
    public void updateItem(Picture item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        setGraphic(
                empty || item == null ? null : imageView(item.getImage(), 16, 16)
        );
    }
}
