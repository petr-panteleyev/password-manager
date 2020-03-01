package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;
import java.util.UUID;
import static org.testng.Assert.assertEquals;

@Test
public class TestFieldContentFilter {

    private static final Card CARD =
        Card.newCard(UUID.randomUUID().toString(), null, List.of(
            new Field(FieldType.STRING, UUID.randomUUID().toString(), "value1"),
            new Field(FieldType.STRING, UUID.randomUUID().toString(), "Value1"),
            new Field(FieldType.STRING, UUID.randomUUID().toString(), "Value2"))
        );

    @DataProvider
    public Object[][] dataProvider() {
        return new Object[][]{
            {CARD, "value", true},
            {CARD, "value1", true},
            {CARD, "ValUe", true},
            {CARD, "ValUe3", false},
        };
    }

    @Test(dataProvider = "dataProvider")
    public void test(Card card, String value, boolean expected) {
        assertEquals(new FieldContentFilter(value).test(card), expected);
    }
}
