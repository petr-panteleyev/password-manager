/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.image.Image;

public enum RecordType {
    EMPTY(Picture.GENERIC, Collections.emptyList()),
    CREDIT_CARD(Picture.CREDIT_CARD, Arrays.asList(
        new Field(FieldType.STRING, "Card Provider", ""),
        new Field(FieldType.STRING, "Credit Card Type", ""),
        new Field(FieldType.CREDIT_CARD_NUMBER, "Card Number", ""),
        new Field(FieldType.STRING, "Expiration Date", ""),
        new Field(FieldType.HIDDEN, "PIN", ""),
        new Field(FieldType.STRING, "Name on Card", ""),
        new Field(FieldType.STRING, "Phone Number", ""),
        new Field(FieldType.HIDDEN, "CVC#", ""),
        new Field(FieldType.STRING, "User Name", ""),
        new Field(FieldType.HIDDEN, "Password", "")
    )),
    CAR(Picture.AUTO, Arrays.asList(
        new Field(FieldType.STRING, "Title", ""),
        new Field(FieldType.STRING, "VIN", ""),
        new Field(FieldType.STRING, "Passport", ""),
        new Field(FieldType.STRING, "Registration", ""),
        new Field(FieldType.STRING, "Engine", ""),
        new Field(FieldType.STRING, "Body", ""),
        new Field(FieldType.STRING, "Plate", "")
    )),
    GLASSES(Picture.GLASSES, Arrays.asList(
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
    PASSPORT(Picture.PASSPORT, Arrays.asList(
        new Field(FieldType.STRING, "Title", ""),
        new Field(FieldType.STRING, "Number", ""),
        new Field(FieldType.STRING, "Issued", ""),
        new Field(FieldType.STRING, "Valid Until", ""),
        new Field(FieldType.STRING, "Issuer", "")
    )),
    EMAIL(Picture.EMAIL, Arrays.asList(
        new Field(FieldType.STRING, "System", ""),
        new Field(FieldType.EMAIL, "E-Mail Address", ""),
        new Field(FieldType.STRING, "User Name", ""),
        new Field(FieldType.HIDDEN, "Password", ""),
        new Field(FieldType.STRING, "IMAP", ""),
        new Field(FieldType.STRING, "IMAP Port", ""),
        new Field(FieldType.STRING, "SMTP", ""),
        new Field(FieldType.STRING, "SMTP Port", "")
    )),
    PASSWORD(Picture.GENERIC, Arrays.asList(
        new Field(FieldType.STRING, "System", ""),
        new Field(FieldType.STRING, "User Name", ""),
        new Field(FieldType.HIDDEN, "Password", ""),
        new Field(FieldType.LINK, "URL", "")
    ));

    private static final String BUNDLE = "org.panteleyev.pwdmanager.RecordType";

    RecordType(Picture picture, List<Field> fieldSet) {
        var bundle = ResourceBundle.getBundle(BUNDLE);
        this.name = bundle.getString(name());

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
