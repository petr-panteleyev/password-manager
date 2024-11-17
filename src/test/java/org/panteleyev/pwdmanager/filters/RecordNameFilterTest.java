/*
 Copyright © 2020-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.filters;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.panteleyev.pwdmanager.model.Card;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RecordNameFilterTest {
    private static final Card CARD =
            new Card("Card Name", null, List.of());

    private static List<Arguments> dataProvider() {
        return List.of(
                Arguments.of(CARD, "Card", true),
                Arguments.of(CARD, "Name", true),
                Arguments.of(CARD, "card name", true),
                Arguments.of(CARD, "D n", true),
                Arguments.of(CARD, "ValUe3", false)
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test(Card card, String value, boolean expected) {
        assertEquals(expected, new RecordNameFilter(value).test(card));
    }
}
