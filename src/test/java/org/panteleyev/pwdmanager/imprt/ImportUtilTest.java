/*
 Copyright © 2021-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.imprt;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.model.ImportRecord;
import org.panteleyev.pwdmanager.model.Picture;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.panteleyev.TestUtil.randomString;

public class ImportUtilTest {
    private static final int SIZE = 10;
    private static final int NEW_SIZE = 3;
    private static final int DELETE_SIZE = 5;

    private static final List<Card> CARDS = IntStream.range(0, SIZE).mapToObj(x -> new Card(
                    UUID.randomUUID(),
                    x,
                    Picture.AIRPLANE,
                    randomString(),
                    emptyList(),
                    "",
                    false,
                    true
            )
    ).toList();

    private static final List<Card> REPLACE = IntStream.range(0, SIZE).mapToObj(x -> {
        var c = CARDS.get(x);
        return new Card(
                c.uuid(),
                c.modified() + 10,
                c.picture(),
                c.name(),
                emptyList(),
                "",
                false,
                true
        );
    }).toList();

    private static final List<Card> DELETE = IntStream.range(0, DELETE_SIZE).mapToObj(x -> {
        var c = CARDS.get(x);
        return new Card(
                c.uuid(),
                c.modified() + 10,
                c.picture(),
                c.name(),
                emptyList(),
                "",
                false,
                false
        );
    }).toList();

    private static final List<Card> ADD = IntStream.range(0, NEW_SIZE).mapToObj(x -> new Card(
                    UUID.randomUUID(),
                    x,
                    Picture.AIRPLANE,
                    randomString(),
                    emptyList(),
                    "",
                    false,
                    true
            )
    ).toList();

    private static List<Arguments> dataProvider() {
        return List.of(
                Arguments.of(CARDS, CARDS, emptyList()),
                Arguments.of(REPLACE, CARDS, emptyList()),
                Arguments.of(CARDS, REPLACE,
                        IntStream.range(0, SIZE).mapToObj(
                                x -> new ImportRecord(ImportAction.REPLACE, CARDS.get(x), REPLACE.get(x))
                        ).toList()
                ),
                Arguments.of(CARDS, ADD,
                        IntStream.range(0, NEW_SIZE).mapToObj(
                                x -> new ImportRecord(ImportAction.ADD, null, ADD.get(x))
                        ).toList()
                ),
                Arguments.of(CARDS, DELETE,
                        IntStream.range(0, DELETE_SIZE).mapToObj(
                                x -> new ImportRecord(ImportAction.DELETE, CARDS.get(x), DELETE.get(x))
                        ).toList()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void testCalculateImport(List<Card> cards, List<Card> toImport, List<ImportRecord> expected) {
        var actual = ImportUtil.calculateImport(cards, toImport);
        assertEquals(expected, actual);
    }
}
