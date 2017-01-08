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
import javafx.beans.property.SimpleBooleanProperty;

public class Category extends Record {
    private SimpleBooleanProperty expanded = new SimpleBooleanProperty();

    public Category(String id, long modified, String name, RecordType type, Picture picture, boolean expanded) {
        super(id, modified, name, type, picture);
        this.expanded.set(expanded);
    }

    public Category(String name, RecordType type, Picture picture) {
        super(name, type, picture);
    }

    @Override
    public Category clone() {
        try {
            Category clone = (Category)super.clone();
            clone.expanded = new SimpleBooleanProperty(expanded.get());
            return clone;
        } catch (Exception ex) {
            // not gonna happen
            throw new RuntimeException(ex);
        }
    }

    public SimpleBooleanProperty expandedProperty() {
        return expanded;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Category) {
            Category that = (Category)o;
            return super.equals(o)
                && this.expanded.get() == that.expanded.get();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(this.expanded.get());
        return hash;
    }
}
