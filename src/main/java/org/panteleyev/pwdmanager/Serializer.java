/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.commons.xml.StartElementWrapper;
import org.panteleyev.commons.xml.XMLEventReaderWrapper;
import org.panteleyev.commons.xml.XMLStreamWriterWrapper;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.WalletRecord;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.panteleyev.pwdmanager.Constants.BUILD_INFO_BUNDLE;

final class Serializer {
    private enum CardClass {
        CARD,
        NOTE,
        UNKNOWN;

        public static CardClass of(String value) {
            try {
                return valueOf(value);
            } catch (Exception ex) {
                return UNKNOWN;
            }
        }
    }

    // Attributes
    private static final QName ATTR_VERSION = new QName("version");
    private static final QName ATTR_CLASS = new QName("recordClass");
    private static final QName ATTR_UUID = new QName("uuid");
    private static final QName ATTR_NAME = new QName("name");
    private static final QName ATTR_TYPE = new QName("type");
    private static final QName ATTR_MODIFIED = new QName("modified");
    private static final QName ATTR_VALUE = new QName("value");
    private static final QName ATTR_PICTURE = new QName("picture");
    private static final QName ATTR_FAVORITE = new QName("favorite");
    private static final QName ATTR_ACTIVE = new QName("active");

    // Tags
    private static final QName WALLET = new QName("wallet");
    private static final QName RECORD = new QName("record");
    private static final QName NOTE = new QName("note");
    private static final QName FIELD = new QName("field");
    private static final QName FIELDS = new QName("fields");
    private static final QName RECORDS = new QName("records");

    static void serialize(OutputStream out, List<WalletRecord> records) {
        try (var w = XMLStreamWriterWrapper.newInstance(out, Set.of(XMLStreamWriterWrapper.Option.LOCAL_DATE_AS_EPOCH_DAY))) {
            w.document(WALLET, () -> {
                w.attribute(ATTR_VERSION, BUILD_INFO_BUNDLE.getString("version"));

                w.element(RECORDS, () -> {
                    for (var r : records) {
                        serializeRecord(w, r);
                    }
                });
            });
        }
    }

    static void deserialize(InputStream in, List<WalletRecord> list) {
        try (var reader = XMLEventReaderWrapper.newInstance(in)) {
            while (reader.hasNext()) {
                var event = reader.nextEvent();

                event.ifStartElement(RECORD, element -> {
                    var record = deserializeRecord(reader, element);
                    if (record != null) {
                        list.add(record);
                    }
                });
            }
        }
    }

    private static void serializeRecord(XMLStreamWriterWrapper w, WalletRecord record) {
        w.element(RECORD, () -> {
            w.attributes(Map.of(
                    ATTR_UUID, record.uuid(),
                    ATTR_NAME, record.name(),
                    ATTR_MODIFIED, record.modified(),
                    ATTR_PICTURE, record.picture(),
                    ATTR_FAVORITE, record.favorite(),
                    ATTR_ACTIVE, record.active()
            ));

            switch (record) {
                case Card card -> {
                    w.attribute(ATTR_CLASS, CardClass.CARD);

                    var fields = card.fields();
                    if (!fields.isEmpty()) {
                        w.element(FIELDS, () -> {
                            for (var f : fields) {
                                serializeField(w, f);
                            }
                        });
                    }

                    w.element(NOTE, card.note());
                }
                case Note note -> w.attribute(ATTR_CLASS, CardClass.NOTE)
                        .text(note.note());
            }
        });
    }

    private static void serializeField(XMLStreamWriterWrapper w, Field field) {
        w.element(FIELD, Map.of(
                ATTR_NAME, field.name(),
                ATTR_TYPE, field.type(),
                ATTR_VALUE, field.value()
        ));
    }

    private static WalletRecord deserializeRecord(XMLEventReaderWrapper reader, StartElementWrapper element) {
        var recordClass = CardClass.of(element.getAttributeValue(ATTR_CLASS));
        if (recordClass == null) {
            return null;
        }
        return switch (recordClass) {
            case CARD -> deserializeCard(reader, element);
            case NOTE -> deserializeNote(reader, element);
            default -> null;
        };
    }

    private static WalletRecord deserializeCard(XMLEventReaderWrapper reader, StartElementWrapper element) {
        var uuid = element.getAttributeValue(ATTR_UUID, UUID.randomUUID());
        var name = element.getAttributeValue(ATTR_NAME);
        var picture = Picture.of(element.getAttributeValue(ATTR_PICTURE));
        var modified = Long.parseLong(element.getAttributeValue(ATTR_MODIFIED));
        var favorite = element.getAttributeValue(ATTR_FAVORITE, false);
        var active = element.getAttributeValue(ATTR_ACTIVE, true);

        var fields = new ArrayList<Field>();
        var note = new StringBuilder();

        while (reader.hasNext()) {
            var peek = reader.peek();
            if (peek.isEmpty()) {
                break;
            }

            var next = peek.get();

            if (next.isEndElement(RECORD)) {
                reader.nextEvent();
                return new Card(uuid, modified, picture, name, fields, note.toString(), favorite, active);
            }

            next.asStartElement(FIELD).ifPresentOrElse(fieldElement -> {
                reader.nextEvent();
                fields.add(deserializeField(fieldElement));
            }, () -> next.asStartElement(NOTE).ifPresentOrElse(_ -> {
                reader.nextEvent();
                note.append(reader.getElementText().orElse(""));
            }, reader::nextEvent));
        }

        return null;
    }

    private static Field deserializeField(StartElementWrapper element) {
        var name = element.getAttributeValue(ATTR_NAME);
        var type = FieldType.valueOf(element.getAttributeValue(ATTR_TYPE));
        var value = Field.deserializeValue(type, element.getAttributeValue(ATTR_VALUE));
        return new Field(type, name, value);
    }

    private static WalletRecord deserializeNote(XMLEventReaderWrapper reader, StartElementWrapper element) {
        var uuid = element.getAttributeValue(ATTR_UUID, UUID.randomUUID());
        var name = element.getAttributeValue(ATTR_NAME);
        var modified = Long.parseLong(element.getAttributeValue(ATTR_MODIFIED));
        var favorite = element.getAttributeValue(ATTR_FAVORITE, false);
        var active = element.getAttributeValue(ATTR_ACTIVE, true);
        var text = reader.getElementText().orElse("");

        return new Note(uuid, name, text, favorite, active, modified);
    }
}
