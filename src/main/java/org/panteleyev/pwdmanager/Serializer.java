/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.WalletRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    private static final String CLASS_ATTR = "recordClass";
    private static final String UUID_ATTR = "uuid";
    private static final String NAME_ATTR = "name";
    private static final String TYPE_ATTR = "type";
    private static final String MODIFIED_ATTR = "modified";
    private static final String VALUE_ATTR = "value";
    private static final String PICTURE_ATTR = "picture";
    private static final String FAVORITE_ATTR = "favorite";
    private static final String ACTIVE_ATTR = "active";

    // Tags
    private static final String FIELD = "field";
    private static final String FIELDS = FIELD + "s";
    private static final String RECORDS = "records";

    private static final DocumentBuilderFactory DOC_FACTORY = DocumentBuilderFactory.newInstance();

    static void serialize(OutputStream out, List<WalletRecord> records) throws ParserConfigurationException,
        TransformerException
    {
        var docBuilder = DOC_FACTORY.newDocumentBuilder();

        var doc = docBuilder.newDocument();
        var rootElement = doc.createElement("wallet");

        doc.appendChild(rootElement);

        var recordsElement = doc.createElement(RECORDS);
        rootElement.appendChild(recordsElement);

        for (var r : records) {
            recordsElement.appendChild(
                serializeRecord(doc, r)
            );
        }

        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }

    static void deserialize(InputStream in, List<WalletRecord> list) throws ParserConfigurationException,
        SAXException, IOException
    {
        var docBuilder = DOC_FACTORY.newDocumentBuilder();

        var doc = docBuilder.parse(in);

        var rootElement = doc.getDocumentElement();
        var records = rootElement.getElementsByTagName("record");
        deserializeRecords(records, list);
    }

    static Element serializeRecord(Document doc, WalletRecord r) {
        var xmlRecord = doc.createElement("record");

        xmlRecord.setAttribute(UUID_ATTR, r.uuid().toString());
        xmlRecord.setAttribute(NAME_ATTR, r.name());
        xmlRecord.setAttribute(MODIFIED_ATTR, Long.toString(r.modified()));
        xmlRecord.setAttribute(PICTURE_ATTR, r.picture().name());
        xmlRecord.setAttribute(FAVORITE_ATTR, Boolean.toString(r.favorite()));
        xmlRecord.setAttribute(ACTIVE_ATTR, Boolean.toString(r.active()));

        if (r instanceof Card card) {
            xmlRecord.setAttribute(CLASS_ATTR, CardClass.CARD.name());

            var fields = card.fields();
            if (!fields.isEmpty()) {
                var fieldsElement = doc.createElement(FIELDS);
                xmlRecord.appendChild(fieldsElement);

                for (var f : fields) {
                    fieldsElement.appendChild(
                        serializeField(doc, f)
                    );
                }
            }

            var note = card.note();
            var noteElement = doc.createElement("note");
            noteElement.setTextContent(note);
            xmlRecord.appendChild(noteElement);
        } else if (r instanceof Note note) {
            xmlRecord.setAttribute(CLASS_ATTR, CardClass.NOTE.name());
            xmlRecord.appendChild(doc.createTextNode(note.note()));
        }

        return xmlRecord;
    }

    static Element serializeField(Document doc, Field f) {
        var e = doc.createElement(FIELD);
        e.setAttribute(NAME_ATTR, f.name());
        e.setAttribute(TYPE_ATTR, f.type().name());
        e.setAttribute(VALUE_ATTR, f.value());
        return e;
    }

    static Field deserializeField(Element e) {
        var name = e.getAttribute(NAME_ATTR);
        var type = FieldType.valueOf(e.getAttribute(TYPE_ATTR));
        var value = e.getAttribute(VALUE_ATTR);

        return new Field(type, name, value);
    }

    static WalletRecord deserializeNote(Element element) {
        var uuid = readUuidAttribute(element);
        var name = element.getAttribute(NAME_ATTR);
        var modified = Long.parseLong(element.getAttribute(MODIFIED_ATTR));
        var text = element.getTextContent();
        var favorite = Boolean.parseBoolean(element.getAttribute(FAVORITE_ATTR));
        var active = readActiveAttribute(element);

        return new Note(uuid, name, text, favorite, active, modified);
    }

    private static void deserializeRecords(NodeList records, List<WalletRecord> list) {
        // children
        for (int i = 0; i < records.getLength(); i++) {
            var item = records.item(i);

            if (item instanceof Element element) {
                var recordClass = CardClass.of(element.getAttribute(CLASS_ATTR));
                if (recordClass != null) {
                    var record = switch (recordClass) {
                        case CARD -> deserializeCard(element);
                        case NOTE -> deserializeNote(element);
                        default -> null;
                    };
                    if (record != null) {
                        list.add(record);
                    }
                }
            }
        }
    }

    static Card deserializeCard(Element element) {
        var uuid = readUuidAttribute(element);
        var name = element.getAttribute(NAME_ATTR);
        var picture = Picture.of(element.getAttribute(PICTURE_ATTR));
        var modified = Long.parseLong(element.getAttribute(MODIFIED_ATTR));
        var favorite = Boolean.parseBoolean(element.getAttribute(FAVORITE_ATTR));
        var active = readActiveAttribute(element);

        // fields
        var fList = element.getElementsByTagName(FIELD);
        var fields = new ArrayList<Field>(fList.getLength());
        for (int i = 0; i < fList.getLength(); i++) {
            var f = deserializeField((Element) fList.item(i));
            fields.add(f);
        }

        // note
        var note = "";
        var notes = element.getElementsByTagName("note");
        if (notes.getLength() > 0) {
            var noteElement = (Element) notes.item(0);
            note = noteElement.getTextContent();
        }

        return new Card(uuid, modified, picture, name, fields, note, favorite, active);
    }

    private static boolean readActiveAttribute(Element element) {
        // For backward compatibility missing 'active' attribute means true
        var activeElement = element.getAttribute(ACTIVE_ATTR);
        return activeElement == null || activeElement.isBlank() || Boolean.parseBoolean(activeElement);
    }

    private static UUID readUuidAttribute(Element element) {
        var uuidString = element.getAttribute(UUID_ATTR);
        if (uuidString == null || uuidString.isEmpty()) {
            return UUID.randomUUID();
        } else {
            return UUID.fromString(uuidString);
        }
    }
}
