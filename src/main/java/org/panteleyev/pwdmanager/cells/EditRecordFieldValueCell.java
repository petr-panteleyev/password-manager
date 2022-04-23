/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.cells;

import javafx.application.Platform;
import javafx.geometry.Dimension2D;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.panteleyev.pwdmanager.EditableField;
import org.panteleyev.pwdmanager.model.CardType;
import java.time.LocalDate;
import static org.panteleyev.fx.combobox.ComboBoxBuilder.comboBox;
import static org.panteleyev.pwdmanager.model.Picture.SMALL_IMAGE_SIZE;
import static org.panteleyev.pwdmanager.model.Picture.imageView;

public class EditRecordFieldValueCell extends TableCell<EditableField, Object> {
    private final TextField textField = new TextField();
    private final ComboBox<CardType> cardTypeComboBox = comboBox(CardType.values(),
        b -> b.withDefaultString("-")
            .withStringConverter(CardType::getName)
            .withImageConverter(CardType::getImage)
            .withImageDimension(new Dimension2D(SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE))
    );
    private final DatePicker datePicker = new DatePicker();

    public EditRecordFieldValueCell() {
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                commitEdit(textField.getText());
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                event.consume();
            }
        });

        cardTypeComboBox.setOnAction(event -> {
            var selected = cardTypeComboBox.getSelectionModel().getSelectedItem();
            commitEdit(selected);
            event.consume();
        });

        datePicker.getEditor().setEditable(false);
        datePicker.setOnAction(event -> {
            var date = datePicker.getValue();
            commitEdit(date);
            event.consume();
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        var item = getItem();
        // TODO: reimplement with switch pattern matching when available
        if (item instanceof CardType cardType) {
            cardTypeComboBox.getSelectionModel().select(cardType);
            setGraphic(cardTypeComboBox);
        } else if (item instanceof LocalDate localDate) {
            datePicker.setValue(localDate);
            setGraphic(datePicker);
        } else {
            setGraphic(textField);
            textField.setText(item.toString());
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
        setupCellView();
    }

    @Override
    protected void updateItem(Object value, boolean empty) {
        super.updateItem(value, empty);

        if (value == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                // TODO: reimplement with switch pattern matching when available
                if (value instanceof CardType cardType) {
                    cardTypeComboBox.getSelectionModel().select(cardType);
                    setGraphic(cardTypeComboBox);
                } else if (value instanceof LocalDate localDate) {
                    datePicker.setValue(localDate);
                    setGraphic(datePicker);
                } else {
                    textField.setText(value.toString());
                    setGraphic(textField);
                }
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setupCellView();
            }
        }
    }

    private void setupCellView() {
        var field = getTableRow().getItem();
        if (field == null) {
            return;
        }
        var value = field.getValue();
        // TODO: reimplement with switch pattern matching when available
        if (value instanceof CardType cardType) {
            setGraphic(imageView(cardType.getImage(), SMALL_IMAGE_SIZE, SMALL_IMAGE_SIZE));
            setText(cardType.getName());
            setContentDisplay(ContentDisplay.LEFT);
        } else {
            setGraphic(null);
            setText(field.toField().getValueAsString());
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }
}
