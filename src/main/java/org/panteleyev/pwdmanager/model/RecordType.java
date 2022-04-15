/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import javafx.scene.image.Image;
import org.panteleyev.pwdmanager.bundles.RecordTypeBundle;
import java.util.List;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD_NUMBER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD_PROVIDER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD_TYPE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CAR_BODY;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ENGINE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_EXPIRATION_DATE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ISSUED;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ISSUER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_LOGIN;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NAME_ON_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_NUMBER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PASSWORD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PHONE_NUMBER;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_PLATE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_REGISTRATION;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SYSTEM;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE_1;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_VALID_UNTIL;

public enum RecordType {
    EMPTY(Picture.GENERIC, List.of()),
    CREDIT_CARD(Picture.CREDIT_CARD, List.of(
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_CARD_PROVIDER), ""),
        new Field(FieldType.CARD_TYPE, fxString(UI_BUNDLE, I18N_CARD_TYPE), CardType.OTHER),
        new Field(FieldType.CREDIT_CARD_NUMBER, fxString(UI_BUNDLE, I18N_CARD_NUMBER), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_EXPIRATION_DATE), ""),
        new Field(FieldType.PIN, "PIN", ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_NAME_ON_CARD), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_PHONE_NUMBER), ""),
        new Field(FieldType.HIDDEN, "CVC#", ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_LOGIN), ""),
        new Field(FieldType.LONG_PASSWORD, fxString(UI_BUNDLE, I18N_PASSWORD), "")
    )),
    CAR(Picture.AUTO, List.of(
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_TITLE_1), ""),
        new Field(FieldType.STRING, "VIN", ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_PASSPORT), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_REGISTRATION), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_ENGINE), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_CAR_BODY), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_PLATE), "")
    )),
    GLASSES(Picture.GLASSES, List.of(
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
    PASSPORT(Picture.PASSPORT, List.of(
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_TITLE_1), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_NUMBER), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_ISSUED), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_VALID_UNTIL), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_ISSUER), "")
    )),
    EMAIL(Picture.EMAIL, List.of(
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_SYSTEM), ""),
        new Field(FieldType.EMAIL, "E-Mail", ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_LOGIN), ""),
        new Field(FieldType.LONG_PASSWORD, fxString(UI_BUNDLE, I18N_PASSWORD), ""),
        new Field(FieldType.STRING, "IMAP", ""),
        new Field(FieldType.STRING, "IMAP Port", ""),
        new Field(FieldType.STRING, "SMTP", ""),
        new Field(FieldType.STRING, "SMTP Port", "")
    )),
    PASSWORD(Picture.GENERIC, List.of(
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_SYSTEM), ""),
        new Field(FieldType.STRING, fxString(UI_BUNDLE, I18N_LOGIN), ""),
        new Field(FieldType.LONG_PASSWORD, fxString(UI_BUNDLE, I18N_PASSWORD), ""),
        new Field(FieldType.LINK, "URL", "")
    ));

    private static final ResourceBundle BUNDLE = getBundle(RecordTypeBundle.class.getCanonicalName());

    private final Picture picture;
    private final List<Field> fieldSet;

    RecordType(Picture picture, List<Field> fieldSet) {
        this.picture = picture;
        this.fieldSet = fieldSet;
    }

    public String getName() {
        return BUNDLE.getString(name());
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
