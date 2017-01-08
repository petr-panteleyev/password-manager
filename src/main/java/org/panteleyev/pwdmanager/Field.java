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

import java.util.Objects;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Field implements Cloneable {
    private SimpleObjectProperty<FieldType> type = new SimpleObjectProperty<>();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty value = new SimpleStringProperty();

    public Field(FieldType type, String name, String value) {
        this.name.set(name);
        this.value.set(value);
        this.type.set(type);
    }

    /**
     * Copy constructor.
     * @param that copy from
     */
    public Field(Field that) {
        this.name.set(that.getName());
        this.value.set(that.getValue());
        this.type.set(that.getType());
    }

    @Override
    public Field clone() {
        try {
            Field result = (Field)super.clone();

            result.type = new SimpleObjectProperty<>(type.get());
            result.name = new SimpleStringProperty(name.get());
            result.value = new SimpleStringProperty(value.get());

            return result;
        } catch (Exception ex) {
            // never gonna happen
            throw new RuntimeException(ex);
        }
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty valueProperty() {
        return value;
    }

    public SimpleObjectProperty<FieldType> typeProperty() {
        return type;
    }

    public String getValue() {
        return value.get();
    }

    public FieldType getType() {
        return type.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Field) {
            Field that = (Field)o;

            return Objects.equals(this.type.get(), that.type.get())
                && Objects.equals(this.name.get(), that.name.get())
                && Objects.equals(this.value.get(), that.value.get());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.type.get());
        hash = 89 * hash + Objects.hashCode(this.name.get());
        hash = 89 * hash + Objects.hashCode(this.value.get());
        return hash;
    }
}
