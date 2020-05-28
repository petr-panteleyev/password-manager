/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import org.panteleyev.fx.BaseDialog;
import java.util.List;
import java.util.ResourceBundle;
import static org.panteleyev.fx.GridFactory.colSpan;
import static org.panteleyev.fx.GridFactory.newGridPane;
import static org.panteleyev.fx.LabelFactory.newLabel;

class AboutDialog extends BaseDialog<Object> implements Styles {
    static final String APP_TITLE = "Password Manager";
    private static final ResourceBundle BUILD_INFO = ResourceBundle.getBundle("org.panteleyev.pwdmanager.buildInfo");

    AboutDialog() {
        super(MainWindowController.CSS_PATH);
        setTitle("About Password Manager");

        var l0 = new Label("Password Manager");
        l0.getStyleClass().add(ABOUT_LABEL);

        var grid = newGridPane(GRID_PANE,
            List.of(colSpan(l0, 2)),
            List.of(colSpan(new Label("Copyright (c) 2016, 2020, Petr Panteleyev"), 2)),
            List.of(new Label("Version:"), newLabel(BUILD_INFO, "version")),
            List.of(new Label("Encryption:"), new Label("256-bit AES"))
        );

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
    }
}
