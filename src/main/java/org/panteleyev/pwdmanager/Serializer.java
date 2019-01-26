/*
 * Copyright (c) 2016, 2019, Petr Panteleyev <petr@panteleyev.org>
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

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
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

class Serializer {
    // Attributes
    private static final String CLASS_ATTR = "recordClass";
    private static final String UUID_ATTR = "uuid";
    private static final String NAME_ATTR = "name";
    private static final String TYPE_ATTR = "type";
    private static final String MODIFIED_ATTR = "modified";
    private static final String VALUE_ATTR = "value";
    private static final String PICTURE_ATTR = "picture";
    private static final String FAVORITE_ATTR = "favorite";

    // Tags
    private static final String FIELD = "field";
    private static final String FIELDS = FIELD + "s";
    private static final String RECORDS = "records";

    private static final DocumentBuilderFactory DOC_FACTORY;

    static void serialize(OutputStream out, List<Card> records) throws ParserConfigurationException,
        TransformerException
    {
        var docBuilder = DOC_FACTORY.newDocumentBuilder();

        var doc = docBuilder.newDocument();
        var rootElement = doc.createElement("wallet");

        doc.appendChild(rootElement);

        var recordsElement = doc.createElement(RECORDS);
        rootElement.appendChild(recordsElement);

        for (var r : records) {
            var recordXml = serializeRecord(doc, r);
            recordsElement.appendChild(recordXml);
        }

        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.transform(new DOMSource(doc), new StreamResult(out));
    }

    static void deserialize(InputStream in, List<Card> list) throws ParserConfigurationException,
        SAXException, IOException
    {
        var docBuilder = DOC_FACTORY.newDocumentBuilder();

        var doc = docBuilder.parse(in);

        var rootElement = doc.getDocumentElement();
        var records = rootElement.getElementsByTagName("record");
        deserializeRecords(records, list);
    }

    static Element serializeRecord(Document doc, Card r) {
        var xmlRecord = doc.createElement("record");
        xmlRecord.setAttribute(CLASS_ATTR, r.getCardClass().name());
        xmlRecord.setAttribute(UUID_ATTR, r.getUuid());
        xmlRecord.setAttribute(NAME_ATTR, r.getName());
        xmlRecord.setAttribute(TYPE_ATTR, r.getType().name());
        xmlRecord.setAttribute(MODIFIED_ATTR, Long.toString(r.getModified()));
        xmlRecord.setAttribute(PICTURE_ATTR, r.getPicture().name());
        xmlRecord.setAttribute(FAVORITE_ATTR, Boolean.toString(r.isFavorite()));

        // Card - serialize fields
        if (r.isCard()) {
            var fields = r.getFields();
            if (!fields.isEmpty()) {
                var fieldsElement = doc.createElement(FIELDS);
                xmlRecord.appendChild(fieldsElement);

                fields.forEach((f) -> {
                    var fe = serializeField(doc, f);
                    fieldsElement.appendChild(fe);
                });
            }

            var note = r.getNote();
            var noteElement = doc.createElement("note");
            noteElement.setTextContent(note);
            xmlRecord.appendChild(noteElement);
        }

        if (r.isNote()) {
            xmlRecord.appendChild(doc.createTextNode(r.getNote()));
        }

        return xmlRecord;
    }

    static Element serializeField(Document doc, Field f) {
        var e = doc.createElement(FIELD);
        e.setAttribute(NAME_ATTR, f.getName());
        e.setAttribute(TYPE_ATTR, f.getType().name());
        e.setAttribute(VALUE_ATTR, f.getValue());
        return e;
    }

    static Field deserializeField(Element e) {
        var name = e.getAttribute(NAME_ATTR);
        var type = FieldType.valueOf(e.getAttribute(TYPE_ATTR));
        var value = e.getAttribute(VALUE_ATTR);

        return new Field(type, name, value);
    }

    static Card deserializeNote(Element element) {
        var uuid = element.getAttribute(UUID_ATTR);
        if (uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
        }
        var name = element.getAttribute(NAME_ATTR);
        long modified = Long.valueOf(element.getAttribute(MODIFIED_ATTR));
        var text = element.getTextContent();
        boolean favorite = Boolean.valueOf(element.getAttribute(FAVORITE_ATTR));

        return Card.newNote(uuid, modified, name, text, favorite);
    }

    private static void deserializeRecords(NodeList records, List<Card> list) {
        // children
        for (int j = 0; j < records.getLength(); j++) {
            var r = records.item(j);

            if (r instanceof Element) {
                var re = (Element) r;

                Card record = null;

                var recordClass = re.getAttribute(CLASS_ATTR);
                if (recordClass != null) {
                    switch (recordClass) {
                        case "CARD":
                            record = deserializeCard(re);
                            break;
                        case "NOTE":
                            record = deserializeNote(re);
                            break;
                    }
                }

                if (record != null) {
                    list.add(record);
                }
            }
        }
    }

    static Card deserializeCard(Element element) {
        var uuid = element.getAttribute(UUID_ATTR);
        if (uuid == null || uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString();
        }
        var name = element.getAttribute(NAME_ATTR);
        var picture = Picture.valueOf(element.getAttribute(PICTURE_ATTR));
        long modified = Long.valueOf(element.getAttribute(MODIFIED_ATTR));
        boolean favorite = Boolean.valueOf(element.getAttribute(FAVORITE_ATTR));

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

        return Card.newCard(uuid, modified, name, picture, fields, note, favorite);
    }

    static {
        DOC_FACTORY = DocumentBuilderFactory.newInstance();
    }
}
