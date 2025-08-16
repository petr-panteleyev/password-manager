/*
 Copyright © 2017-2025 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.commons.xml.XMLStreamWriterWrapper;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.WalletRecord;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.panteleyev.pwdmanager.Constants.BUILD_INFO_BUNDLE;

public class Serializer {
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

    private static final String SCHEMA_URL = "/xsd/password-manager.xsd";

    private static final Schema SCHEMA;
    private static final DefaultHandler HANDLER = new DefaultHandler() {
        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }
    };

    static {
        var schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            SCHEMA = schemaFactory.newSchema(Serializer.class.getResource(SCHEMA_URL));
        } catch (SAXException e) {
            // There should not be any exceptions so just fail.
            throw new RuntimeException(e);
        }
    }

    public static void serialize(OutputStream out, List<WalletRecord> records) {
        try (var w = XMLStreamWriterWrapper.newInstance(out)) {
            w.document(WALLET, () -> {
                w.attribute(ATTR_VERSION, BUILD_INFO_BUNDLE.getString(ATTR_VERSION.getLocalPart()));

                w.element(RECORDS, () -> {
                    for (var r : records) {
                        serializeRecord(w, r);
                    }
                });
            });
        }
    }

    public static List<WalletRecord> deserialize(InputStream in) throws ParserConfigurationException,
            SAXException, IOException {

        var factory = DocumentBuilderFactory.newInstance();
        factory.setSchema(SCHEMA);
        factory.setValidating(false);

        var docBuilder = factory.newDocumentBuilder();
        docBuilder.setErrorHandler(HANDLER);

        var doc = docBuilder.parse(in);

        var rootElement = doc.getDocumentElement();
        var records = rootElement.getElementsByTagName(RECORD.getLocalPart());
        return deserializeRecords(records);
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

                    w.textElement(NOTE, card.note());
                }
                case Note note -> w.attribute(ATTR_CLASS, CardClass.NOTE)
                        .textElement(NOTE, note.note());
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

    private static List<WalletRecord> deserializeRecords(NodeList records) {
        var list = new ArrayList<WalletRecord>(records.getLength());

        // children
        for (int i = 0; i < records.getLength(); i++) {
            var item = records.item(i);

            if (item instanceof Element element) {
                var recordClass = CardClass.of(element.getAttribute(ATTR_CLASS.getLocalPart()));
                if (recordClass != null) {
                    var record = switch (recordClass) {
                        case CARD -> deserializeCard(element);
                        case NOTE -> deserializeNote(element);
                        case UNKNOWN -> null;
                    };
                    if (record != null) {
                        list.add(record);
                    }
                }
            }
        }

        return list;
    }

    private static Card deserializeCard(Element element) {
        var uuid = readUuidAttribute(element);
        var name = element.getAttribute(ATTR_NAME.getLocalPart());
        var picture = Picture.of(element.getAttribute(ATTR_PICTURE.getLocalPart()));
        var modified = Long.parseLong(element.getAttribute(ATTR_MODIFIED.getLocalPart()));
        var favorite = Boolean.parseBoolean(element.getAttribute(ATTR_FAVORITE.getLocalPart()));
        var active = readActiveAttribute(element);

        // fields
        var fList = element.getElementsByTagName(FIELD.getLocalPart());
        var fields = new ArrayList<Field>(fList.getLength());
        for (int i = 0; i < fList.getLength(); i++) {
            var f = deserializeField((Element) fList.item(i));
            fields.add(f);
        }

        // note
        var note = "";
        var notes = element.getElementsByTagName(NOTE.getLocalPart());
        if (notes.getLength() > 0) {
            var noteElement = (Element) notes.item(0);
            note = noteElement.getTextContent();
        }

        return new Card(uuid, modified, picture, name, fields, note, favorite, active);
    }

    private static Field deserializeField(Element e) {
        var name = e.getAttribute(ATTR_NAME.getLocalPart());
        var type = FieldType.valueOf(e.getAttribute(ATTR_TYPE.getLocalPart()));
        var value = Field.deserializeValue(type, e.getAttribute(ATTR_VALUE.getLocalPart()));

        return new Field(type, name, value);
    }

    private static WalletRecord deserializeNote(Element element) {
        var uuid = readUuidAttribute(element);
        var name = element.getAttribute(ATTR_NAME.getLocalPart());
        var modified = Long.parseLong(element.getAttribute(ATTR_MODIFIED.getLocalPart()));
        var favorite = Boolean.parseBoolean(element.getAttribute(ATTR_FAVORITE.getLocalPart()));
        var active = readActiveAttribute(element);

        // note
        var note = "";
        var notes = element.getElementsByTagName(NOTE.getLocalPart());
        if (notes.getLength() > 0) {
            var noteElement = (Element) notes.item(0);
            note = noteElement.getTextContent();
        } else {
            // for backward compatibility we also read text content if <note> child is missing
            note = element.getTextContent();
        }

        return new Note(uuid, name, note, favorite, active, modified);
    }

    private static boolean readActiveAttribute(Element element) {
        // For backward compatibility missing 'active' attribute means true
        var activeElement = element.getAttribute(ATTR_ACTIVE.getLocalPart());
        return activeElement.isBlank() || Boolean.parseBoolean(activeElement);
    }

    private static UUID readUuidAttribute(Element element) {
        var uuidString = element.getAttribute(ATTR_UUID.getLocalPart());
        return uuidString.isEmpty() ? UUID.randomUUID() : UUID.fromString(uuidString);
    }
}
