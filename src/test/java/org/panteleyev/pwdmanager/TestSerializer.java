/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.CardType;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.Picture;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
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
        var card = new Card(
            UUID.randomUUID(),
            System.currentTimeMillis(),
            Picture.AMEX,
            UUID.randomUUID().toString(),
            List.of(
                new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                new Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                new Field(FieldType.CARD_TYPE, UUID.randomUUID().toString(), CardType.MASTERCARD)
            ),
            UUID.randomUUID().toString(),
            true, false
        );

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
            {new Field(FieldType.CARD_TYPE, UUID.randomUUID().toString(), CardType.UNION_PAY)},
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
        var note = new Note(UUID.randomUUID().toString());

        var doc = docBuilder.newDocument();

        var e = Serializer.serializeRecord(doc, note);
        var restored = Serializer.deserializeNote(e);
        assertEquals(restored, note);
    }
}
