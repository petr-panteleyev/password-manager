/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;

public final class EditableField {
    private final SimpleObjectProperty<FieldType> type = new SimpleObjectProperty<>();
    private final SimpleStringProperty name = new SimpleStringProperty();
    private final SimpleObjectProperty<Object> value = new SimpleObjectProperty<>();

    public EditableField(FieldType type, String name, Object value) {
        this.type.set(type);
        this.name.set(name);
        this.value.set(value);
    }

    public EditableField(Field field) {
        type.set(field.type());
        name.set(field.name());
        value.set(field.value());
    }

    public Field toField() {
        return new Field(type.get(), name.get(), value.get());
    }

    public SimpleObjectProperty<FieldType> typeProperty() {
        return type;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ObjectProperty<Object> valueProperty() {
        return value;
    }

    public FieldType getType() {
        return type.get();
    }

    public String getName() {
        return name.get();
    }

    public Object getValue() {
        return value.get();
    }
}
