/*
 Copyright © 2020-2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.filters.RecordNameFilter;
import org.panteleyev.pwdmanager.model.Card;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;

@Test
public class TestRecordNameFilter {
    private static final Card CARD =
            new Card("Card Name", null, List.of());

    @DataProvider
    public Object[][] dataProvider() {
        return new Object[][]{
                {CARD, "Card", true},
                {CARD, "Name", true},
                {CARD, "card name", true},
                {CARD, "D n", true},
                {CARD, "ValUe3", false},
        };
    }

    @Test(dataProvider = "dataProvider")
    public void test(Card card, String value, boolean expected) {
        assertEquals(new RecordNameFilter(value).test(card), expected);
    }
}
