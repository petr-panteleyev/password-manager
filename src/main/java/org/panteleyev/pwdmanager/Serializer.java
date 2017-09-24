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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.control.TreeItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Serializer {
    // Attributes
    private static final String ID_ATTR         = "id";
    private static final String NAME_ATTR       = "name";
    private static final String TYPE_ATTR       = "type";
    private static final String MODIFIED_ATTR   = "modified";
    private static final String VALUE_ATTR      = "value";
    private static final String PICTURE_ATTR    = "picture";
    private static final String EXPANDED_ATTR   = "expanded";
    private static final String TARGET_ID_ATTR  = "targetId";

    // Tags
    private static final String FIELD           = "field";
    private static final String FIELDS          = FIELD + "s";
    private static final String RECORDS         = "records";

    private static final DocumentBuilderFactory DOC_FACTORY;

    public static void serialize(OutputStream out, TreeItem<Record> rootItem) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
		DocumentBuilder docBuilder = DOC_FACTORY.newDocumentBuilder();

        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("root");

        doc.appendChild(rootElement);

        Element e = serializeTreeItem(doc, rootItem);
        rootElement.appendChild(e);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(out);
        transformer.transform(source, result);
    }

    public static TreeItem<Record> deserialize(InputStream in) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder docBuilder = DOC_FACTORY.newDocumentBuilder();

        Document doc = docBuilder.parse(in);

        Element rootElement = doc.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n instanceof Element && ((Element)n).getTagName().equals("Category")) {
                return deserializeCategory((Element)n);
            }
        }

        return null;
    }

    public static Element serializeTreeItem(Document doc, TreeItem<Record> treeItem) {
        Record r = treeItem.getValue();

        Element xmlRecord = doc.createElement(r.getClass().getSimpleName());
        xmlRecord.setAttribute(ID_ATTR, r.getId());
        xmlRecord.setAttribute(NAME_ATTR, r.getName());
        xmlRecord.setAttribute(TYPE_ATTR, r.getType().name());
        xmlRecord.setAttribute(MODIFIED_ATTR, Long.toString(r.getModified()));
        xmlRecord.setAttribute(PICTURE_ATTR, r.getPicture().name());

        // Category
        if (r instanceof Category) {
            xmlRecord.setAttribute(EXPANDED_ATTR, Boolean.toString(((Category)r).expandedProperty().get()));
        }

        if (r instanceof Link) {
            xmlRecord.setAttribute(TARGET_ID_ATTR, ((Link)r).getTargetId());
        }

        // Card - serialize fields
        if (r instanceof Card) {
            List<Field> fields = ((Card)r).getFields();
            if (!fields.isEmpty()) {
                Element fieldsElement = doc.createElement(FIELDS);
                xmlRecord.appendChild(fieldsElement);

                fields.forEach((f) -> {
                    Element fe = serializeField(doc, f);
                    fieldsElement.appendChild(fe);
                });
            }

            String note = ((Card)r).getNote();
            Element noteElement = doc.createElement("note");
            noteElement.setTextContent(note);
            xmlRecord.appendChild(noteElement);
        }

        if (r instanceof Note) {
            xmlRecord.appendChild(doc.createTextNode(((Note)r).getText()));
        }

        // Children if any
        if (!treeItem.getChildren().isEmpty()) {
            Element children = doc.createElement(RECORDS);
            xmlRecord.appendChild(children);

            treeItem.getChildren().forEach((child) -> {
                Element e = serializeTreeItem(doc, child);
                children.appendChild(e);
            });
        }

        return xmlRecord;
    }

    public static Element serializeField(Document doc, Field f) {
        Element e = doc.createElement(FIELD);
        e.setAttribute(NAME_ATTR, f.getName());
        e.setAttribute(TYPE_ATTR, f.getType().name());
        e.setAttribute(VALUE_ATTR, f.getValue());
        return e;
    }

    public static Field deserializeField(Element e) {
        String name = e.getAttribute(NAME_ATTR);
        FieldType type = FieldType.valueOf(e.getAttribute(TYPE_ATTR));
        String value = e.getAttribute(VALUE_ATTR);

        return new Field(type, name, value);
    }

    public static TreeItem<Record> deserializeNote(Element element) {
        String id = element.getAttribute(ID_ATTR);
        String name = element.getAttribute(NAME_ATTR);
        long modified = Long.valueOf(element.getAttribute(MODIFIED_ATTR));
        String text = element.getTextContent();

        return new TreeItem<>(new Note(id, modified, name, text));
    }

    public static TreeItem<Record> deserializeLink(Element element) {
        String targetId = element.getAttribute(TARGET_ID_ATTR);
        return new TreeItem<>(new Link(targetId));
    }

    public static TreeItem<Record> deserializeCategory(Element element) {
        String id = element.getAttribute(ID_ATTR);
        String name = element.getAttribute(NAME_ATTR);
        RecordType type = RecordType.valueOf(element.getAttribute(TYPE_ATTR));
        Picture picture = Picture.valueOf(element.getAttribute(PICTURE_ATTR));
        long modified = Long.valueOf(element.getAttribute(MODIFIED_ATTR));

        String expandedString = element.getAttribute(EXPANDED_ATTR);
        if (expandedString.isEmpty()) {
            expandedString = "false";
        }
        boolean expanded = Boolean.valueOf(expandedString);

        Category category = new Category(id, modified, name, type, picture, expanded);
        TreeItem<Record> result = new TreeItem<>(category);
        result.setExpanded(expanded);

        category.expandedProperty().bind(result.expandedProperty());

        result.expandedProperty().addListener((x,oldValue,newValue) -> {
            if (oldValue != newValue) {
                Platform.runLater(() -> MainWindowController.getMainWindow().writeDocument());
            }
        });

        // children
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node n = childNodes.item(i);

            if (n instanceof Element && ((Element)n).getTagName().equals(RECORDS)) {
                NodeList records = n.getChildNodes();
                for (int j = 0; j < records.getLength(); j++) {
                    Node r = records.item(j);

                    if (r instanceof Element) {
                        Element re = (Element)r;

                        TreeItem<Record> recordItem = null;
                        switch (re.getTagName()) {
                            case "Category" :
                                recordItem = deserializeCategory(re);
                                break;
                            case "Card" :
                                recordItem = deserializeCard(re);
                                break;
                            case "Note" :
                                recordItem = deserializeNote(re);
                                break;
                            case "Link" :
                                recordItem = deserializeLink(re);
                                break;
                        }

                        if (recordItem == null) {
                            throw new IllegalStateException("Illegal file format");
                        }

                        result.getChildren().add(recordItem);
                    }
                }

                break;
            }
        }

        return result;
    }

    public static TreeItem<Record> deserializeCard(Element element) {
        String id = element.getAttribute(ID_ATTR);
        String name = element.getAttribute(NAME_ATTR);
        Picture picture = Picture.valueOf(element.getAttribute(PICTURE_ATTR));
        long modified = Long.valueOf(element.getAttribute(MODIFIED_ATTR));

        // fields
        NodeList fList = element.getElementsByTagName(FIELD);
        List<Field> fields = new ArrayList<>(fList.getLength());
        for (int i = 0; i < fList.getLength(); i++) {
            Field f = deserializeField((Element)fList.item(i));
            fields.add(f);
        }

        // note
        String note = "";
        NodeList notes = element.getElementsByTagName("note");
        if (notes.getLength() > 0) {
            Element noteElement = (Element)notes.item(0);
            note = noteElement.getTextContent();
        }

        return new TreeItem<>(new Card(id, modified, name, picture, fields, note));
    }

    static {
        DOC_FACTORY = DocumentBuilderFactory.newInstance();
    }
}
