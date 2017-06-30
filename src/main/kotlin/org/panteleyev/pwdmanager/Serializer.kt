/*
 * Copyright (c) 2017, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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

package org.panteleyev.pwdmanager

import javafx.application.Platform
import javafx.scene.control.TreeItem
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.InputStream
import java.io.OutputStream
import java.util.ArrayList
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

internal object Serializer {
    // Attributes
    private const val ID_ATTR = "id"
    private const val NAME_ATTR = "name"
    private const val TYPE_ATTR = "type"
    private const val MODIFIED_ATTR = "modified"
    private const val VALUE_ATTR = "value"
    private const val PICTURE_ATTR = "picture"
    private const val EXPANDED_ATTR = "expanded"
    private const val TARGET_ID_ATTR = "targetId"

    // Tags
    private const val FIELD = "field"
    private const val FIELDS = FIELD + "s"
    private const val RECORDS = "records"

    private val DOC_FACTORY: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()

    fun serialize(out: OutputStream, rootItem: TreeItem<Record>) {
        val docBuilder = DOC_FACTORY.newDocumentBuilder()

        val doc = docBuilder.newDocument()
        val rootElement = doc.createElement("root")

        doc.appendChild(rootElement)

        val e = serializeTreeItem(doc, rootItem)
        rootElement.appendChild(e)

        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        val source = DOMSource(doc)
        val result = StreamResult(out)
        transformer.transform(source, result)
    }

    fun deserialize(source: InputStream): TreeItem<Record>? {
        val docBuilder = DOC_FACTORY.newDocumentBuilder()

        val doc = docBuilder.parse(source)

        val rootElement = doc.documentElement
        val nodes = rootElement.childNodes

        for (i in 0..nodes.length - 1) {
            val n = nodes.item(i)
            if (n is Element && n.tagName == "Category") {
                return deserializeCategory(n)
            }
        }

        return null
    }

    fun serializeTreeItem(doc: Document, treeItem: TreeItem<Record>): Element {
        val r = treeItem.value

        return doc.createElement(treeItem.value.javaClass.simpleName).apply {
            setAttribute(ID_ATTR, r.id)
            setAttribute(NAME_ATTR, r.name)
            setAttribute(TYPE_ATTR, r.type.name)
            setAttribute(MODIFIED_ATTR, java.lang.Long.toString(r.modified))
            setAttribute(PICTURE_ATTR, r.picture.name)

            // Category
            if (r is Category) {
                setAttribute(EXPANDED_ATTR, java.lang.Boolean.toString(treeItem.isExpanded))
            }

            if (r is Link) {
                setAttribute(TARGET_ID_ATTR, r.targetId)
            }

            // Card - serialize fields
            if (r is Card) {
                val fields = r.fields
                if (!fields.isEmpty()) {
                    val fieldsElement = doc.createElement(FIELDS)
                    appendChild(fieldsElement)

                    fields.forEach {
                        val fe = serializeField(doc, it)
                        fieldsElement.appendChild(fe)
                    }
                }

                val note = r.note
                val noteElement = doc.createElement("note")
                noteElement.textContent = note
                appendChild(noteElement)
            }

            if (r is Note) {
                appendChild(doc.createTextNode(r.text))
            }

            // Children if any
            if (!treeItem.children.isEmpty()) {
                val children = doc.createElement(RECORDS)
                appendChild(children)

                treeItem.children.forEach {
                    val e = serializeTreeItem(doc, it)
                    children.appendChild(e)
                }
            }
        }
    }

    fun serializeField(doc: Document, f: Field): Element {
        return doc.createElement(FIELD).apply {
            setAttribute(NAME_ATTR, f.name)
            setAttribute(TYPE_ATTR, f.type.name)
            setAttribute(VALUE_ATTR, f.value)
        }
    }

    fun deserializeField(e: Element): Field {
        val name = e.getAttribute(NAME_ATTR)
        val type = FieldType.valueOf(e.getAttribute(TYPE_ATTR))
        val value = e.getAttribute(VALUE_ATTR)

        return Field(type, name, value)
    }

    fun deserializeNote(element: Element): TreeItem<Record> {
        val id = element.getAttribute(ID_ATTR)
        val name = element.getAttribute(NAME_ATTR)
        val modified = java.lang.Long.valueOf(element.getAttribute(MODIFIED_ATTR))!!
        val text = element.textContent

        return TreeItem(Note(id = id, modified = modified, name = name, text = text))
    }

    fun deserializeLink(element: Element): TreeItem<Record> {
        val id = element.getAttribute(ID_ATTR)
        val targetId = element.getAttribute(TARGET_ID_ATTR)
        return TreeItem(Link(id, targetId))
    }

    fun deserializeCategory(element: Element): TreeItem<Record> {
        val id = element.getAttribute(ID_ATTR)
        val name = element.getAttribute(NAME_ATTR)
        val type = RecordType.valueOf(element.getAttribute(TYPE_ATTR))
        val picture = Picture.valueOf(element.getAttribute(PICTURE_ATTR))
        val modified = java.lang.Long.valueOf(element.getAttribute(MODIFIED_ATTR))!!

        var expandedString = element.getAttribute(EXPANDED_ATTR)
        if (expandedString.isEmpty()) {
            expandedString = "false"
        }
        val expanded = java.lang.Boolean.valueOf(expandedString)!!

        val category = Category(id, modified, name, type, picture, expanded)
        val result = TreeItem<Record>(category)
        result.isExpanded = expanded

        result.expandedProperty().addListener { _, oldValue, newValue ->
            if (oldValue != newValue) {
                Platform.runLater { MainWindowController.mainWindow?.writeDocument() }
            }
        }

        // children
        val childNodes = element.childNodes
        for (i in 0..childNodes.length - 1) {
            val n = childNodes.item(i)

            if (n is Element && n.tagName == RECORDS) {
                val records = n.getChildNodes()
                for (j in 0..records.length - 1) {
                    val r = records.item(j)

                    if (r is Element) {
                        val recordItem: TreeItem<Record> = when (r.tagName) {
                            "Category" -> deserializeCategory(r)
                            "Card" -> deserializeCard(r)
                            "Note" -> deserializeNote(r)
                            "Link" -> deserializeLink(r)
                            else -> throw IllegalStateException("Illegal file format")
                        }

                        result.children.add(recordItem)
                    }
                }

                break
            }
        }

        return result
    }

    fun deserializeCard(element: Element): TreeItem<Record> {
        val id = element.getAttribute(ID_ATTR)
        val name = element.getAttribute(NAME_ATTR)
        val picture = Picture.valueOf(element.getAttribute(PICTURE_ATTR))
        val modified = java.lang.Long.valueOf(element.getAttribute(MODIFIED_ATTR))!!

        // fields
        val fList = element.getElementsByTagName(FIELD)
        val fields = ArrayList<Field>(fList.length)
        for (i in 0..fList.length - 1) {
            val f = deserializeField(fList.item(i) as Element)
            fields.add(f)
        }

        // note
        var note = ""
        val notes = element.getElementsByTagName("note")
        if (notes.length > 0) {
            val noteElement = notes.item(0) as Element
            note = noteElement.textContent
        }

        return TreeItem(Card(id, modified, name, picture, fields, note))
    }
}
