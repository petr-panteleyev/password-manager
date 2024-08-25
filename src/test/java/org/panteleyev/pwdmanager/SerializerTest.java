/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.junit.jupiter.api.Test;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.CardType;
import org.panteleyev.pwdmanager.model.Field;
import org.panteleyev.pwdmanager.model.FieldType;
import org.panteleyev.pwdmanager.model.Note;
import org.panteleyev.pwdmanager.model.Picture;
import org.panteleyev.pwdmanager.model.WalletRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.panteleyev.TestUtil.randomString;

public class SerializerTest {
    private static final List<WalletRecord> RECORDS = List.of(
            new Card(
                    UUID.randomUUID(),
                    System.currentTimeMillis(),
                    Picture.AMEX,
                    randomString(),
                    List.of(
                            new Field(FieldType.STRING, randomString(), randomString()),
                            new Field(FieldType.HIDDEN, randomString(), randomString()),
                            new Field(FieldType.CARD_TYPE, randomString(), CardType.MASTERCARD),
                            new Field(FieldType.DATE, randomString(), LocalDate.now())
                    ),
                    randomString(),
                    true, false
            ),
            new Card(
                    UUID.randomUUID(),
                    System.currentTimeMillis(),
                    Picture.AMEX,
                    randomString(),
                    List.of(
                            new Field(FieldType.STRING, randomString(), randomString()),
                            new Field(FieldType.HIDDEN, randomString(), randomString()),
                            new Field(FieldType.CARD_TYPE, randomString(), CardType.MASTERCARD)
                    ),
                    randomString(),
                    true, false
            ),
            new Note(randomString()),
            new Card(
                    UUID.randomUUID(),
                    System.currentTimeMillis(),
                    Picture.AMEX,
                    randomString(),
                    List.of(
                            new Field(FieldType.STRING, randomString(), randomString()),
                            new Field(FieldType.HIDDEN, randomString(), randomString()),
                            new Field(FieldType.CARD_TYPE, randomString(), CardType.MASTERCARD)
                    ),
                    randomString(),
                    true, false
            ),
            new Note(randomString()),
            new Note(randomString())
    );

    @Test
    public void testSerializeAndDeserialize() {
        var out = new ByteArrayOutputStream();
        Serializer.serialize(out, RECORDS);

        var bytes = out.toByteArray();
        XmlValidator.validate(new ByteArrayInputStream(bytes));

        var deserialized = new ArrayList<WalletRecord>();
        Serializer.deserialize(new ByteArrayInputStream(bytes), deserialized);

        assertEquals(RECORDS, deserialized);
    }
}
