/*
 Copyright © 2020-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.panteleyev.pwdmanager.model.FieldType.STRING;

public class TestFieldContentFilter {

    private static final Card CARD =
            new Card(UUID.randomUUID().toString(), null, List.of(
                    new Field(STRING, UUID.randomUUID().toString(), "value1"),
                    new Field(STRING, UUID.randomUUID().toString(), "Value1"),
                    new Field(STRING, UUID.randomUUID().toString(), "Value2"))
            );

    private static List<Arguments> dataProvider() {
        return List.of(
                Arguments.of(CARD, "value", true),
                Arguments.of(CARD, "value1", true),
                Arguments.of(CARD, "ValUe", true),
                Arguments.of(CARD, "ValUe3", false)
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test(Card card, String value, boolean expected) {
        assertEquals(expected, new FieldContentFilter(value).test(card));
    }
}
