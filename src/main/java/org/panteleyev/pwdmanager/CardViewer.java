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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class CardViewer extends BorderPane {
    private final GridPane grid = new GridPane();
    private final Label noteLabel = new Label();
    private final Label noteViewer = new Label();

    public CardViewer() {
        VBox vBox = new VBox();
        vBox.setFillWidth(true);

        grid.setHgap(5);
        grid.setVgap(5);

        vBox.getChildren().addAll(grid, noteLabel, noteViewer);

        noteLabel.setGraphic(new ImageView(Picture.NOTE.getImage()));
        noteLabel.setText("Notes");

        noteLabel.getStyleClass().add("noteLabel");
        noteViewer.getStyleClass().add("noteViewer");

        noteViewer.setVisible(false);
        noteLabel.setVisible(false);

        ScrollPane scroll = new ScrollPane(vBox);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("whiteBackground");

        setCenter(scroll);
        grid.setStyle("-fx-alignment: TOP-CENTER;");
    }

    void setData(List<FieldWrapper> items, String note) {
        grid.getChildren().clear();

        int y = 1;
        for (FieldWrapper field : items) {
            Label nameLabel = new Label(field.getName());
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

        ContextMenu menu = new ContextMenu(copyMenuItem);
        return menu;
    }

    private void onCopy(Field field) {
        Clipboard cb = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();

        String value = field.getValue();
        if (field.getType() == FieldType.CREDIT_CARD_NUMBER) {
            // remove all spaces from credit card number
            value = value.trim().replaceAll(" ", "");
        }

        content.putString(value);
        cb.setContent(content);
    }
}
