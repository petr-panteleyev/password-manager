/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;

final class EditableField {
    private final SimpleObjectProperty<FieldType> type = new SimpleObjectProperty<>();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleStringProperty value = new SimpleStringProperty();

    EditableField(FieldType type, String name, String value) {
        this.type.set(type);
        this.name.set(name);
        this.value.set(value);
    }

    EditableField(Field field) {
        type.set(field.type());
        name.set(field.name());
        value.set(field.value());
    }

    Field toField() {
        return new Field(type.get(), name.get(), value.get());
    }

    SimpleObjectProperty<FieldType> typeProperty() {
        return type;
    }

    StringProperty nameProperty() {
        return name;
    }

    StringProperty valueProperty() {
        return value;
    }

    FieldType getType() {
        return type.get();
    }

    String getName() {
        return name.get();
    }

    String getValue() {
        return value.get();
    }
}
