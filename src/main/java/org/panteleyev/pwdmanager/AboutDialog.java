/*
 * Copyright (c) 2017, 2020, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.panteleyev.pwdmanager;

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
