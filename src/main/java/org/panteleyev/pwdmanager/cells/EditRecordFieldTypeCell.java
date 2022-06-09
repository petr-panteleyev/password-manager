/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import org.panteleyev.pwdmanager.EditableField;
import org.panteleyev.pwdmanager.model.CardType;
import org.panteleyev.pwdmanager.model.FieldType;

import java.time.LocalDate;

import static org.panteleyev.fx.combobox.ComboBoxBuilder.comboBox;

public class EditRecordFieldTypeCell extends TableCell<EditableField, FieldType> {
    private final ComboBox<FieldType> typeComboBox = comboBox(FieldType.values());

    public EditRecordFieldTypeCell() {
        typeComboBox.setOnAction(event -> {
            var editableField = getTableRow().getItem();
            var oldType = editableField.getType();
            var newType = typeComboBox.getSelectionModel().getSelectedItem();
            var newValue = convertValue(editableField.getValue(), oldType, newType);
            editableField.valueProperty().set(newValue);
            commitEdit(newType);
            event.consume();
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        typeComboBox.getSelectionModel().select(getItem());
        setGraphic(typeComboBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getString());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    protected void updateItem(FieldType type, boolean empty) {
        super.updateItem(type, empty);

        if (type == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                typeComboBox.getSelectionModel().select(type);
                setGraphic(typeComboBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setGraphic(null);
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }

    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }

    private Object convertValue(Object value, FieldType oldType, FieldType newType) {
        return switch (newType) {
            case CARD_TYPE -> CardType.of(value.toString());
            case DATE, EXPIRATION_MONTH -> convertToLocalDate(value);
            default -> value.toString();
        };
    }

    private static LocalDate convertToLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return LocalDate.ofEpochDay(localDate.toEpochDay());
        } else {
            return LocalDate.now();
        }
    }
}
