/*
 * Copyright (c) 2016, 2018, Petr Panteleyev <petr@panteleyev.org>
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

import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.ResourceBundle;

class CardViewer extends BorderPane implements Styles {
    private final ResourceBundle rb = PasswordManagerApplication.getBundle();

    private final GridPane grid = new GridPane();
    private final Label    noteLabel =
            new Label(rb.getString("label.notesNoSemicolon"), new ImageView(Picture.NOTE.getImage()));
    private final Label    noteViewer = new Label();

    CardViewer() {
        initialize();
    }

    private void initialize() {
        grid.getStyleClass().add(GRID_PANE);
        grid.setAlignment(Pos.TOP_CENTER);

        var vBox = new VBox(
                grid,
                noteLabel,
                noteViewer
        );

        noteLabel.getStyleClass().add("noteLabel");
        noteViewer.getStyleClass().add("noteViewer");

        var pane = new ScrollPane(vBox);
        pane.getStyleClass().add("whiteBackground");
        pane.setFitToWidth(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setCenter(pane);

        BorderPane.setAlignment(pane, Pos.CENTER);
    }

    void setData(List<FieldWrapper> items, String note) {
        grid.getChildren().clear();

        int y = 1;
        for (var field : items) {
            var nameLabel = new Label(field.getName());
            nameLabel.getStyleClass().add("fieldName");

            Labeled valueLabel;
            if (field.getType() == FieldType.LINK) {
                valueLabel = new Hyperlink(field.getValue());
                ((Hyperlink)valueLabel).setOnAction(e -> onHyperlinkClick(field.getValue()));
            } else {
                valueLabel = new Label(field.getType() == FieldType.HIDDEN ?
                        "***" : field.getValue());

                valueLabel.setOnMouseClicked(event -> {
                    if (event.getClickCount() > 1) {
                        onContentViewDoubleClick(field, valueLabel);
                    }
                });
            }

            valueLabel.setContextMenu(createContextMenu(field));

            grid.add(nameLabel, 1, y);
            grid.add(valueLabel, 2, y++);
        }

        noteViewer.setVisible(!note.isEmpty());
        noteLabel.setVisible(!note.isEmpty());
        noteViewer.setText(note);
    }

    private void onHyperlinkClick(String url) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void onContentViewDoubleClick(FieldWrapper field, Labeled label) {
        switch (field.getType()) {
            case HIDDEN:
                field.toggleShow();
                label.setText(field.getShow()? field.getValue() : "***");
                break;
        }
    }

    private ContextMenu createContextMenu(FieldWrapper field) {
        MenuItem copyMenuItem = new MenuItem("Copy " + field.getName());
        copyMenuItem.setOnAction(x -> onCopy(field));

        return new ContextMenu(copyMenuItem);
    }

    private void onCopy(Field field) {
        var cb = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();

        var value = field.getValue();
        if (field.getType() == FieldType.CREDIT_CARD_NUMBER) {
            // remove all spaces from credit card number
            value = value.trim().replaceAll(" ", "");
        }

        content.putString(value);
        cb.setContent(content);
    }
}
