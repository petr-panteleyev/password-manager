/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.CardType;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.Picture;

import java.util.List;

import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.TabFactory.tab;
import static org.panteleyev.pwdmanager.Constants.MASK;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.PasswordManagerApplication.showDocument;
import static org.panteleyev.pwdmanager.Styles.STYLE_CARD_CONTENT_TITLE;
import static org.panteleyev.pwdmanager.Styles.STYLE_FIELD_NAME;
import static org.panteleyev.pwdmanager.Styles.STYLE_FIELD_VALUE;
import static org.panteleyev.pwdmanager.Styles.STYLE_GRID_PANE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_COPY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NOTES;
import static org.panteleyev.pwdmanager.model.Picture.SMALL_IMAGE_SIZE;
import static org.panteleyev.pwdmanager.model.Picture.imageView;

final class CardViewer extends BorderPane {
    private static final double LEFT_WIDTH = 40.0;
    private static final double RIGHT_WIDTH = 100.0 - LEFT_WIDTH;


    private final GridPane grid = new GridPane();
    private final TextArea noteArea = new TextArea();

    private final TabPane tabPane = new TabPane();
    private final Tab fieldTab;
    private final Tab noteTab = tab("Note", noteArea);


    CardViewer() {
        grid.getStyleClass().add(STYLE_GRID_PANE);

        var col1 = new ColumnConstraints();
        col1.setPercentWidth(LEFT_WIDTH);
        col1.setHgrow(Priority.NEVER);
        col1.setHalignment(HPos.RIGHT);
        var col2 = new ColumnConstraints();
        col2.setPercentWidth(RIGHT_WIDTH);
        col2.setHgrow(Priority.NEVER);
        col2.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().setAll(col1, col2);
        grid.setFocusTraversable(false);

        var vBox = new VBox(grid);
        VBox.setMargin(grid, new Insets(10, 5, 10, 5));

        var pane = new ScrollPane(vBox);
        pane.getStyleClass().add("whiteBackground");
        pane.setFitToWidth(true);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        noteTab.setGraphic(Picture.imageView(Picture.NOTE.getImage(), SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE));
        noteTab.getStyleClass().add(STYLE_CARD_CONTENT_TITLE);

        fieldTab = tab("", pane);
        fieldTab.getStyleClass().add(STYLE_CARD_CONTENT_TITLE);

        noteArea.setEditable(false);
        setCenter(tabPane);
    }

    void clearData() {
        grid.getChildren().clear();
        tabPane.getTabs().remove(fieldTab);
        tabPane.getTabs().remove(noteTab);
    }

    void setData(Card card, List<FieldWrapper> items) {
        if (!tabPane.getTabs().contains(fieldTab)) {
            tabPane.getTabs().addFirst(fieldTab);
        }

        grid.getChildren().clear();

        int y = 0;
        for (var field : items) {
            var nameLabel = new Label(field.getName());
            nameLabel.getStyleClass().add(STYLE_FIELD_NAME);

            Labeled valueLabel;

            switch (field.getType()) {
                case LINK -> {
                    valueLabel = new Hyperlink(field.getValueAsString());
                    ((Hyperlink) valueLabel).setOnAction(_ -> onHyperlinkClick(field.getValueAsString()));
                }
                case CARD_TYPE -> {
                    var cardType = (CardType) field.getValue();
                    valueLabel = new Label(cardType.getName(),
                            imageView(cardType.getImage(), SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE));
                }
                default -> {
                    valueLabel = new Label(field.getType().isMasked() ? MASK : field.getValueAsString());
                    valueLabel.getStyleClass().add(STYLE_FIELD_VALUE);

                    valueLabel.setOnMouseClicked(event -> {
                        if (event.getClickCount() > 1) {
                            onContentViewDoubleClick(field, valueLabel);
                        }
                    });
                }
            }

            valueLabel.setContextMenu(createContextMenu(field));
            valueLabel.setWrapText(true);

            grid.add(nameLabel, 0, y);
            grid.add(valueLabel, 1, y++);

            GridPane.setValignment(nameLabel, VPos.TOP);
            GridPane.setValignment(valueLabel, VPos.TOP);
        }

        var note = card.note();

        var fieldTab = tabPane.getTabs().getFirst();
        fieldTab.setText(card.name());
        fieldTab.setGraphic(Picture.imageView(card.picture().getImage(), 24, 24));

        if (note.isEmpty()) {
            tabPane.getTabs().remove(noteTab);
        } else {
            if (!tabPane.getTabs().contains(noteTab)) {
                tabPane.getTabs().add(noteTab);
            }
            noteArea.setText(note);
        }

        noteTab.setText(fxString(UI_BUNDLE, I18N_NOTES));
        tabPane.getSelectionModel().selectFirst();
    }

    void setData(Note note) {
        grid.getChildren().clear();

        tabPane.getTabs().remove(fieldTab);
        if (!tabPane.getTabs().contains(noteTab)) {
            tabPane.getTabs().add(noteTab);
        }

        noteTab.setText(note.name());
        noteArea.setText(note.note());
    }

    private void onHyperlinkClick(String url) {
        showDocument(url);
    }

    private void onContentViewDoubleClick(FieldWrapper field, Labeled label) {
        if (!field.getType().isMasked()) {
            return;
        }

        field.toggleShow();
        label.setText(field.getShow() ? field.getValueAsString() : MASK);
    }

    private ContextMenu createContextMenu(FieldWrapper field) {
        var copyMenuItem = new MenuItem(UI_BUNDLE.getString(I18N_COPY) + " '" + field.getName() + "'");
        copyMenuItem.setOnAction(_ -> onCopy(field.getField()));

        return new ContextMenu(copyMenuItem);
    }

    private void onCopy(Field field) {
        var cb = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();

        var value = field.getValueAsString();
        if (field.type() == FieldType.CREDIT_CARD_NUMBER) {
            // remove all spaces from credit card number
            value = value.replaceAll(" ", "");
        }

        content.putString(value);
        cb.setContent(content);
    }
}
