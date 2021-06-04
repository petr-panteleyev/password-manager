/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import org.panteleyev.pwdmanager.model.Picture;

public class PictureListCell extends ListCell<Picture> {
    @Override
    public void updateItem(Picture item, boolean empty) {
        super.updateItem(item, empty);

        setText(null);
        setGraphic(
            empty || item == null ? null : new ImageView(item.getImage())
        );
    }
}
