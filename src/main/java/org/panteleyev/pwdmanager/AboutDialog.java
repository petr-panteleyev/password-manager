package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.panteleyev.fx.BaseDialog;

import java.util.ResourceBundle;

class AboutDialog extends BaseDialog implements Styles {
    static final String APP_TITLE = "Password Manager";
    private static final String BUILD_INFO = "org.panteleyev.pwdmanager.buildInfo";

    AboutDialog() {
        super(MainWindowController.CSS_PATH);

        var buildInfo = ResourceBundle.getBundle(BUILD_INFO);


        setTitle("About Password Manager");

        var grid = new GridPane();
        grid.getStyleClass().add(GRID_PANE);

        var l0 = new Label("Password Manager");
        l0.getStyleClass().add(ABOUT_LABEL);

        var l1 = new Label("Copyright (c) 2016, 2020, Petr Panteleyev");

        grid.addRow(0, l0);
        grid.addRow(1, l1);
        grid.addRow(2, new Label("Version:"), new Label(buildInfo.getString("version")));
        grid.addRow(3, new Label("Encryption:"), new Label("256-bit AES"));

        GridPane.setColumnSpan(l0, 2);
        GridPane.setColumnSpan(l1, 2);

        getDialogPane().setContent(grid);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
    }
}
