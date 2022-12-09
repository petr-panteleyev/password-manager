/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.panteleyev.pwdmanager.model.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSerializer {
    private static final DocumentBuilder DOCUMENT_BUILDER;

    static {
        try {
            var docFactory = DocumentBuilderFactory.newInstance();
            DOCUMENT_BUILDER = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
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

        var doc = DOCUMENT_BUILDER.newDocument();

        var e = Serializer.serializeRecord(doc, card);

        var restored = Serializer.deserializeCard(e);

        assertEquals(card, restored);
    }

    private static List<Arguments> dataProvider() {
        return List.of(
                Arguments.of(new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString())),
                Arguments.of(new Field(FieldType.HIDDEN, UUID.randomUUID().toString(), UUID.randomUUID().toString())),
                Arguments.of(new Field(FieldType.LINK, UUID.randomUUID().toString(), "1024")),
                Arguments.of(new Field(FieldType.CARD_TYPE, UUID.randomUUID().toString(), CardType.UNION_PAY))
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void testFieldSerialization(Field field) {
        var doc = DOCUMENT_BUILDER.newDocument();

        var e = Serializer.serializeField(doc, field);
        var restored = Serializer.deserializeField(e);

        assertEquals(field, restored);
    }

    @Test
    public void testNoteSerialization() {
        var note = new Note(UUID.randomUUID().toString());

        var doc = DOCUMENT_BUILDER.newDocument();

        var e = Serializer.serializeRecord(doc, note);
        var restored = Serializer.deserializeNote(e);
        assertEquals(note, restored);
    }
}
