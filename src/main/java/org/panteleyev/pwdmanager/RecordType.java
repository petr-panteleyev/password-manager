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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.scene.image.Image;

public enum RecordType {
    EMPTY("Empty", Picture.GENERIC, Collections.EMPTY_LIST),
    CREDIT_CARD("Credit card", Picture.CREDIT_CARD, Arrays.asList(
        new Field(FieldType.STRING, "Card Provider", ""),
        new Field(FieldType.STRING, "Credit Card Type", ""),
        new Field(FieldType.STRING, "Card Number", ""),
        new Field(FieldType.STRING, "Expiration Date", ""),
        new Field(FieldType.HIDDEN, "PIN", ""),
        new Field(FieldType.STRING, "Name on Card", ""),
        new Field(FieldType.STRING, "Phone Number", ""),
        new Field(FieldType.STRING, "CVC#", ""),
        new Field(FieldType.STRING, "User Name", ""),
        new Field(FieldType.HIDDEN, "Password", "")
    )),
    CAR("Car", Picture.AUTO, Arrays.asList(
        new Field(FieldType.STRING, "Title", ""),
        new Field(FieldType.STRING, "VIN", ""),
        new Field(FieldType.STRING, "Passport", ""),
        new Field(FieldType.STRING, "Registration", ""),
        new Field(FieldType.STRING, "Engine", ""),
        new Field(FieldType.STRING, "Body", ""),
        new Field(FieldType.STRING, "Plate", "")
    )),
    GLASSES("Glasses", Picture.GLASSES, Arrays.asList(
        new Field(FieldType.STRING, "Right (O.D.) SPH", ""),
        new Field(FieldType.STRING, "Right CYL", ""),
        new Field(FieldType.HIDDEN, "Right AXIS", ""),
        new Field(FieldType.STRING, "Left (O.S.) SPH", ""),
        new Field(FieldType.STRING, "Left CYL", ""),
        new Field(FieldType.STRING, "Left AXIS", ""),
        new Field(FieldType.STRING, "Pupil Distance (mm)", ""),
        new Field(FieldType.STRING, "Doctor's Name", ""),
        new Field(FieldType.STRING, "Doctor's Phone #", ""),
        new Field(FieldType.STRING, "Other Information", "")
    )),
    PASSPORT("Passport", Picture.PASSPORT, Arrays.asList(
        new Field(FieldType.STRING, "Title", ""),
        new Field(FieldType.STRING, "Number", ""),
        new Field(FieldType.STRING, "Issued", ""),
        new Field(FieldType.STRING, "Valid Until", ""),
        new Field(FieldType.STRING, "Issuer", "")
    )),
    EMAIL("E-Mail", Picture.EMAIL, Arrays.asList(
        new Field(FieldType.STRING, "System", ""),
        new Field(FieldType.EMAIL, "E-Mail Address", ""),
        new Field(FieldType.STRING, "User Name", ""),
        new Field(FieldType.HIDDEN, "Password", ""),
        new Field(FieldType.STRING, "IMAP", ""),
        new Field(FieldType.STRING, "IMAP Port", ""),
        new Field(FieldType.STRING, "SMTP", ""),
        new Field(FieldType.STRING, "SMTP Port", "")
    )),
    PASSWORD("Password", Picture.GENERIC, Arrays.asList(
        new Field(FieldType.STRING, "System", ""),
        new Field(FieldType.STRING, "User Name", ""),
        new Field(FieldType.HIDDEN, "Password", ""),
        new Field(FieldType.LINK, "URL", "")
    ));

    private RecordType(String name, Picture picture, List<Field> fieldSet) {
        this.name = name;
        this.picture = picture;
        this.fieldSet = fieldSet;
    }

    private final String name;
    private final Picture picture;
    private final List<Field> fieldSet;

    public String getName() {
        return name;
    }

    public Picture getPicture() {
        return picture;
    }

    public Image getImage() {
        return picture.getImage();
    }

    public List<Field> getFieldSet() {
        return fieldSet;
    }
}
