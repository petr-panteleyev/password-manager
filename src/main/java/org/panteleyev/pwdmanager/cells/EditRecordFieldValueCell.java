/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.cells;

import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import org.panteleyev.pwdmanager.EditableField;
import org.panteleyev.pwdmanager.model.CardType;
import static org.panteleyev.fx.combobox.ComboBoxBuilder.comboBox;
import static org.panteleyev.pwdmanager.model.Picture.SMALL_IMAGE_SIZE;
import static org.panteleyev.pwdmanager.model.Picture.imageView;

public class EditRecordFieldValueCell extends TableCell<EditableField, EditableField> {
    private final TextField textField = new TextField();
    private final ComboBox<CardType> cardTypeComboBox = comboBox(CardType.values(),
        b -> b.withDefaultString("-")
            .withStringConverter(CardType::getName)
            .withImageConverter(CardType::getImage)
            .withImageDimension(new Dimension2D(SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE))
    );

    public EditRecordFieldValueCell() {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                var item = getItem();
                item.valueProperty().set(textField.getText());
                commitEdit(item);
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                event.consume();
            }
        });

        cardTypeComboBox.setOnAction(event -> {
            var selected = cardTypeComboBox.getSelectionModel().getSelectedItem();
            var item = getItem();
            item.valueProperty().set(selected);
            commitEdit(item);
            event.consume();
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        var item = getItem();
        // TODO: reimplement with switch pattern matching when available
        if (item.getValue() instanceof CardType cardType) {
            cardTypeComboBox.getSelectionModel().select(cardType);
            setGraphic(cardTypeComboBox);
        } else {
            setGraphic(textField);
            textField.setText(item.getValue().toString());
            Platform.runLater(() -> {
                textField.requestFocus();
                textField.selectAll();
            });
        }
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setupCellView(getItem());
    }

    @Override
    protected void updateItem(EditableField field, boolean empty) {
        super.updateItem(field, empty);

        if (field == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                // TODO: reimplement with switch pattern matching when available
                if (field.getValue() instanceof CardType cardType) {
                    cardTypeComboBox.getSelectionModel().select(cardType);
                    setGraphic(cardTypeComboBox);
                } else {
                    textField.setText(field.getValue().toString());
                    setGraphic(textField);
                }
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setupCellView(field);
            }
        }
    }

    private String getString() {
        return getItem() == null ? "" : getItem().getValue().toString();
    }

    private void setupCellView(EditableField field) {
        // TODO: reimplement with switch pattern matching when available
        if (field.getValue() instanceof CardType cardType) {
            setGraphic(imageView(cardType.getImage(), SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE));
            setText(cardType.getName());
            setContentDisplay(ContentDisplay.LEFT);
        } else {
            setGraphic(null);
            setText(field.getValue().toString());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
