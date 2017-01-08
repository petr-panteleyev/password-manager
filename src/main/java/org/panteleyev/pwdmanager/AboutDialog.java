/*
 * Copyright (c) 2016, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AboutDialog extends Dialog {
    private static final String BUILD = "1.0.0-alpha";


    public AboutDialog() {
        setTitle("About Password Manager");

        getDialogPane().getButtonTypes().addAll(ButtonType.OK);

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);

        int y = 1;

        Label mainLabel = new Label("Password Manager");
        Font font = mainLabel.getFont();
        mainLabel.setFont(Font.font(font.getFamily(), FontWeight.BOLD, font.getSize() * 1.5));

        pane.add(mainLabel, 1, y++, 2, 1);
        pane.add(new Label("Copyright (c) 2016, 2017, Petr Panteleyev <petr@panteleyev.org>"), 1, y++, 2, 1);
        pane.add(new Label("Build:"), 1, y);
        pane.add(new Label(BUILD), 2, y++);
        pane.add(new Label("Encryption: "), 1, y);
        pane.add(new Label("256-bit AES"), 2, y++);

        getDialogPane().setContent(pane);
    }
}
