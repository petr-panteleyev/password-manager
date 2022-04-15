/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.cells;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import org.panteleyev.pwdmanager.EditableField;
import org.panteleyev.pwdmanager.model.CardType;
import org.panteleyev.pwdmanager.model.FieldType;
import static org.panteleyev.fx.combobox.ComboBoxBuilder.comboBox;

public class EditRecordFieldTypeCell extends TableCell<EditableField, EditableField> {
    private final ComboBox<FieldType> typeComboBox = comboBox(FieldType.values());

    public EditRecordFieldTypeCell() {
        typeComboBox.setOnAction(event -> {
            var item = getItem();
            var oldType = getItem().getType();
            var newType = typeComboBox.getSelectionModel().getSelectedItem();
            var newValue = convertValue(getItem().getValue(), oldType, newType);
            item.typeProperty().set(newType);
            item.valueProperty().set(newValue);
            commitEdit(item);
            event.consume();
        });
    }

    @Override
    public void startEdit() {
        super.startEdit();
        typeComboBox.getSelectionModel().select(getItem().getType());
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
    protected void updateItem(EditableField field, boolean empty) {
        super.updateItem(field, empty);

        if (field == null || empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                typeComboBox.getSelectionModel().select(getItem().getType());
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
        return getItem() == null ? "" : getItem().getType().toString();
    }

    private Object convertValue(Object value, FieldType oldType, FieldType newType) {
        if (newType == FieldType.CARD_TYPE) {
            return CardType.of(value.toString());
        } else {
            return value.toString();
        }
    }
}
