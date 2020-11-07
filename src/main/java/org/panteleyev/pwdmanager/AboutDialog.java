/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ButtonType;
import org.panteleyev.fx.BaseDialog;
import java.util.List;
import java.util.ResourceBundle;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.fx.grid.GridBuilder.gridCell;
import static org.panteleyev.fx.grid.GridBuilder.gridPane;
import static org.panteleyev.fx.grid.GridRowBuilder.gridRow;

class AboutDialog extends BaseDialog<Object> implements Styles {
    static final String APP_TITLE = "Password Manager";
    private static final ResourceBundle BUILD_INFO = ResourceBundle.getBundle("org.panteleyev.pwdmanager.buildInfo");

    AboutDialog() {
        super(MainWindowController.CSS_PATH);
        setTitle("About Password Manager");

        var l0 = label("Password Manager");
        l0.getStyleClass().add(ABOUT_LABEL);

        var grid = gridPane(
            List.of(
                gridRow(gridCell(l0, 2, 1)),
                gridRow(gridCell(label("Copyright (c) 2016, 2020, Petr Panteleyev"), 2, 1)),
                gridRow(label("Version:"), label(fxString(BUILD_INFO, "version"))),
                gridRow(label("Build:"), label(fxString(BUILD_INFO, "timestamp"))),
                gridRow(label("Encryption:"), label("256-bit AES"))
            ), b -> b.withStyle(Styles.GRID_PANE)
        );

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
    }
}
