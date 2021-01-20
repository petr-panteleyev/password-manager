/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ButtonType;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import java.util.List;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.grid.GridBuilder.gridCell;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.Constants.BUILD_INFO_BUNDLE;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Constants.STYLE_ABOUT_LABEL;
import static org.panteleyev.pwdmanager.Constants.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.Options.options;

class AboutDialog extends BaseDialog<Object> {

    AboutDialog(Controller owner) {
        super(owner, options().getDialogCssFileUrl());
        setTitle("About Password Manager");

        var l0 = label(APP_TITLE);
        l0.getStyleClass().add(STYLE_ABOUT_LABEL);

        var grid = gridPane(
            List.of(
                gridRow(gridCell(l0, 2, 1)),
                gridRow(gridCell(label("Copyright (c) 2016, 2021, Petr Panteleyev"), 2, 1)),
                gridRow(label(fxString(RB, "Version", ":")), label(fxString(BUILD_INFO_BUNDLE, "version"))),
                gridRow(label(fxString(RB, "Build", ":")), label(fxString(BUILD_INFO_BUNDLE, "timestamp"))),
                gridRow(label(fxString(RB, "Encryption", ":")), label("256-bit AES"))
            ), b -> b.withStyle(STYLE_GRID_PANE)
        );

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
    }
}
