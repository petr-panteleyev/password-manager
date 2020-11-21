/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Arrays;
import java.util.UUID;
import static org.testng.Assert.assertEquals;

public class TestSerializer {
    private DocumentBuilder docBuilder;

    @BeforeClass
    public void initFactory() throws Exception {
        var docFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docFactory.newDocumentBuilder();
    }

    @Test
    public void testCardSerialization() {
        var card = Card.newCard(UUID.randomUUID().toString(), Picture.AMEX, Arrays.asList(
            new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
            new Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        ), UUID.randomUUID().toString());

        var doc = docBuilder.newDocument();

        var e = Serializer.serializeRecord(doc, card);

        var restored = Serializer.deserializeCard(e);

        assertEquals(restored, card);
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
    public void testFieldSerialization(Field field) {
        var doc = docBuilder.newDocument();

        var e = Serializer.serializeField(doc, field);
        var restored = Serializer.deserializeField(e);

        assertEquals(restored, field);
    }

    @Test
    public void testNoteSerialization() {
        var note = Card.newNote(UUID.randomUUID().toString(), UUID.randomUUID().toString(), true);

        var doc = docBuilder.newDocument();

        var e = Serializer.serializeRecord(doc, note);
        var restored = Serializer.deserializeNote(e);
        assertEquals(restored, note);
    }
}
