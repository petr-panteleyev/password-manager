/*
 * Copyright (c) 2018, 2019, Petr Panteleyev <petr@panteleyev.org>
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
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Arrays;
import java.util.UUID;

public class TestSerializer {
    private DocumentBuilder docBuilder;

    @BeforeClass
    public void initFactory() throws Exception {
        var docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
    }

    @Test
    public void testCardSerialization() throws Exception {
        var card = Card.newCard(UUID.randomUUID().toString(), Picture.AMEX, Arrays.asList(
                new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                new Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        ), UUID.randomUUID().toString());

        var doc = docBuilder.newDocument();

        var e = Serializer.serializeRecord(doc, card);

        var restored = Serializer.deserializeCard(e);

        Assert.assertEquals(restored, card);
    }

    @DataProvider(name = "testFieldToFromJson")
    public Object[][] testFieldToFromJsonDataProvider() {
        return new Object[][]{
                {new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString())},
                {new Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString())},
                {new Field(FieldType.LINK, UUID.randomUUID().toString(), "1024")},
        };
    }

    @Test(dataProvider = "testFieldToFromJson")
    public void testFieldSerialization(Field field) throws Exception {
        var doc = docBuilder.newDocument();

        var e = Serializer.serializeField(doc, field);
        var restored = Serializer.deserializeField(e);

        Assert.assertEquals(restored, field);
    }

    @Test
    public void testNoteSerialization() throws Exception {
        var note = Card.newNote(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true);

        var doc = docBuilder.newDocument();

        var e = Serializer.serializeRecord(doc, note);
        var restored = Serializer.deserializeNote(e);
        Assert.assertEquals(restored, note);
    }
}
