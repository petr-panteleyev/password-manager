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
import java.util.UUID;
import javafx.scene.input.DataFormat;

public abstract class Record implements Cloneable {
    public static final String     MIME_TYPE  = "application/x-org-panteleyev-password-manager-record-id";
    public static final DataFormat DATA_FORMAT = new DataFormat(MIME_TYPE);

    private String id;

    private final long        modified;
    private final RecordType  type;
    private final Picture     picture;
    private final String      name;

    public Record(String name, RecordType type, Picture picture) {
        this(UUID.randomUUID().toString(), System.currentTimeMillis(), name, type, picture);
    }

    public Record(String id, String name, RecordType type, Picture picture) {
        this(id, System.currentTimeMillis(), name, type, picture);
    }

    public Record(String id, long modified, String name, RecordType type, Picture picture) {
        this.id = id;
        this.modified = modified;
        this.name = name;
        this.type = type;
        this.picture = picture;
    }

    @Override
    public Record clone() {
        try {
            Record clone = (Record)super.clone();
            return clone;
        } catch (Exception ex) {
            // not gonna happen
            throw new RuntimeException(ex);
        }
    }

    public final Record cloneWithNewId() {
        Record newRecord = this.clone();
        newRecord.id = UUID.randomUUID().toString();
        return newRecord;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RecordType getType() {
        return type;
    }

    public Picture getPicture() {
        return picture;
    }

    public long getModified() {
        return modified;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Record) {
            Record that = (Record)o;

            return Objects.equals(this.id, that.id)
                && Objects.equals(this.name, that.name)
                && Objects.equals(this.type, that.type)
                && Objects.equals(this.picture, that.picture)
                && Objects.equals(this.modified, that.modified);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + (int) (this.modified ^ (this.modified >>> 32));
        hash = 47 * hash + Objects.hashCode(this.name);
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + Objects.hashCode(this.picture);
        return hash;
    }
}
