package org.panteleyev.pwdmanager.model;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import javafx.scene.input.DataFormat;
import org.panteleyev.pwdmanager.Picture;
import org.panteleyev.pwdmanager.RecordType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Card {
    private static final String MIME_TYPE = "application/x-org-panteleyev-password-manager-record-id";
    public static final DataFormat DATA_FORMAT = new DataFormat(MIME_TYPE);

    private final CardClass cardClass;

    // Base
    private final String uuid;
    private final long modified;
    private final RecordType type;
    private final Picture picture;
    private final String name;
    private final boolean favorite;

    // Card
    private final List<Field> fields = new ArrayList<>();
    private final String note;

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

    public Card(CardClass cardClass, String uuid, long modified, RecordType type, Picture picture, String name,
                List<Field> fields, String note, boolean favorite)
    {
        this.cardClass = cardClass;
        this.uuid = uuid;
        this.modified = modified;
        this.type = type;
        this.picture = picture;
        this.name = name;
        if (fields != null) {
            this.fields.addAll(fields);
        }
        this.note = note;
        this.favorite = favorite;
    }

    public Card(Card card) {
        this(card.getCardClass(), card.getUuid(), card.getModified(), card.getType(), card.getPicture(),
                card.getName(), card.getFields(), card.getNote(), card.isFavorite());
    }

    public Card copyWithNewUuid() {
        return new Card(cardClass, UUID.randomUUID().toString(), modified, type, picture,
            name, fields, note, favorite);
    }

    public Card setFavorite(boolean favorite) {
        return new Card(cardClass, uuid, modified, type, picture,
            name, fields, note, favorite);
    }

    public CardClass getCardClass() {
        return cardClass;
    }

    public boolean isCard() {
        return CardClass.CARD.equals(cardClass);
    }

    public boolean isNote() {
        return CardClass.NOTE.equals(cardClass);
    }

    public String getUuid() {
        return uuid;
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

    public List<Field> getFields() {
        return fields;
    }

    public String getNote() {
        return note;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o instanceof Card) {
            var that = (Card) o;
            return Objects.equals(this.cardClass, that.cardClass)
                    && Objects.equals(this.uuid, that.uuid)
                    && this.modified == that.modified
                    && Objects.equals(this.name, that.name)
                    && Objects.equals(this.type, that.type)
                    && Objects.equals(this.picture, that.picture)
                    && Objects.equals(this.fields, that.fields)
                    && Objects.equals(this.note, that.note)
                    && this.favorite == that.favorite;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardClass, uuid, modified, name, type, picture, fields, note, favorite);
    }

    @Override
    public String toString() {
        return "[Card:"
                + " cardClass=" + cardClass
                + " uuid=" + uuid
                + " modified=" + modified
                + " name=" + name
                + " type=" + type
                + " picture=" + picture
                + " note=" + note
                + " favorite" + favorite
                + " fields=" + fields;
    }
}
