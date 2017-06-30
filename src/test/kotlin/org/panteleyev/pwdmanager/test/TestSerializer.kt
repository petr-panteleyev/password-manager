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

package org.panteleyev.pwdmanager.test

import javafx.scene.control.TreeItem
import org.panteleyev.pwdmanager.Card
import org.panteleyev.pwdmanager.Category
import org.panteleyev.pwdmanager.Field
import org.panteleyev.pwdmanager.FieldType
import org.panteleyev.pwdmanager.Link
import org.panteleyev.pwdmanager.Note
import org.panteleyev.pwdmanager.Picture
import org.panteleyev.pwdmanager.Record
import org.panteleyev.pwdmanager.RecordType
import org.panteleyev.pwdmanager.Serializer
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.UUID
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

internal class TestSerializer {
    private val docBuilder: DocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    @Test
    fun testCardSerialization() {
        val card = Card(name = UUID.randomUUID().toString(), picture = Picture.AMEX, fields = listOf(
                Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        ), note = UUID.randomUUID().toString())

        val doc = docBuilder.newDocument()

        val e = Serializer.serializeTreeItem(doc, TreeItem<Record>(card))

        val restored = Serializer.deserializeCard(e)

        Assert.assertEquals(restored.value, card)
    }

    @Test
    fun testCategorySerialization() {
        val category = Category(name = UUID.randomUUID().toString(), type = RecordType.EMPTY, picture = Picture.FOLDER)

        val doc = docBuilder.newDocument()

        val e = Serializer.serializeTreeItem(doc, TreeItem<Record>(category))
        val restored = Serializer.deserializeCategory(e)
        Assert.assertEquals(restored.value, category)
    }

    @DataProvider(name = "testFieldToFromJson")
    fun testFieldToFromJsonDataProvider(): Array<Array<Any>> {
        return arrayOf(
                arrayOf<Any>(Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString())),
                arrayOf<Any>(Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString())),
                arrayOf<Any>(Field(FieldType.LINK, UUID.randomUUID().toString(), "1024"))
        )
    }

    @Test(dataProvider = "testFieldToFromJson")
    fun testFieldSerialization(field: Field) {
        val doc = docBuilder.newDocument()

        val e = Serializer.serializeField(doc, field)
        val restored = Serializer.deserializeField(e)

        Assert.assertEquals(restored, field)
    }

    @Test
    fun testNoteSerialization() {
        val note = Note(name = UUID.randomUUID().toString(), text = UUID.randomUUID().toString())

        val doc = docBuilder.newDocument()

        val e = Serializer.serializeTreeItem(doc, TreeItem<Record>(note))
        val restored = Serializer.deserializeNote(e)
        Assert.assertEquals(restored.value, note)
    }

    @Test
    fun testLinkSerialization() {
        val link = Link(targetId = UUID.randomUUID().toString())

        val doc = docBuilder.newDocument()

        val e = Serializer.serializeTreeItem(doc, TreeItem<Record>(link))
        val restored = Serializer.deserializeLink(e)
        Assert.assertEquals(restored.value, link)
    }
}
