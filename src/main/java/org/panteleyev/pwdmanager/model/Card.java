/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager.model;

import javafx.scene.input.DataFormat;
import org.panteleyev.pwdmanager.Picture;
import org.panteleyev.pwdmanager.RecordType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Card(CardClass cardClass, String uuid, long modified,
                   RecordType type, Picture picture, String name,
                   List<Field>fields, String note, boolean favorite
) {
    private static final String MIME_TYPE = "application/x-org-panteleyev-password-manager-record-id";
    public static final DataFormat DATA_FORMAT = new DataFormat(MIME_TYPE);


    public static Card newCard(String name, Picture picture, List<Field> fields) {
        return newCard(UUID.randomUUID().toString(), name, picture, fields, "");
    }

    public static Card newCard(String name, Picture picture, List<Field> fields, String note) {
        return newCard(UUID.randomUUID().toString(), name, picture, fields, note);
    }

    public static Card newCard(String uuid, String name, Picture picture, List<Field> fields, String note) {
        return newCard(uuid, System.currentTimeMillis(), name, picture, fields, note, false);
    }

    public static Card newCard(String uuid, long modified, String name, Picture picture, List<Field> fields,
                               String note, boolean favorite)
    {
        return new Card(CardClass.CARD, uuid, modified, RecordType.EMPTY, picture, name, fields, note, favorite);
    }

    public static Card newNote(String name, String text, boolean favorite) {
        return newNote(UUID.randomUUID().toString(), System.currentTimeMillis(), name, text, favorite);
    }

    public static Card newNote(String uuid, String name, String text, boolean favorite) {
        return newNote(uuid, System.currentTimeMillis(), name, text, favorite);
    }

    public static Card newNote(String uuid, long modified, String name, String text, boolean favorite) {
        return new Card(CardClass.NOTE, uuid, modified, RecordType.EMPTY, Picture.NOTE, name, List.of(), text, favorite);
    }

    public Card {
        if (fields != null) {
            fields = new ArrayList<>(fields);
        }
    }

    public Card(Card card) {
        this(card.cardClass, card.uuid, card.modified, card.type, card.picture,
            card.name, card.fields, card.note, card.favorite);
    }

    public Card copyWithNewUuid() {
        return new Card(cardClass, UUID.randomUUID().toString(), modified, type, picture,
            name, fields, note, favorite);
    }

    public Card setFavorite(boolean favorite) {
        return new Card(cardClass, uuid, modified, type, picture,
            name, fields, note, favorite);
    }

    public boolean isCard() {
        return CardClass.CARD.equals(cardClass);
    }

    public boolean isNote() {
        return CardClass.NOTE.equals(cardClass);
    }
}
