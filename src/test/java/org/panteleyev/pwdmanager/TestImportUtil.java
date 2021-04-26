/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.CardClass;
import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.model.ImportRecord;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import static java.util.Collections.emptyList;
import static org.testng.Assert.assertEquals;

public class TestImportUtil {
    private static final int SIZE = 10;
    private static final int NEW_SIZE = 3;

    private static final List<Card> CARDS = IntStream.range(0, SIZE).mapToObj(x -> new Card(
            CardClass.CARD,
            UUID.randomUUID().toString(),
            x,
            RecordType.CAR,
            Picture.AIRPLANE,
            UUID.randomUUID().toString(),
            emptyList(),
            "",
            false
        )
    ).toList();

    private static final List<Card> REPLACE = IntStream.range(0, SIZE).mapToObj(x -> {
        var c = CARDS.get(x);
        return new Card(
            c.cardClass(),
            c.uuid(),
            c.modified() + 10,
            c.type(),
            c.picture(),
            c.name(),
            emptyList(),
            "",
            false
        );
    }).toList();

    private static final List<Card> ADD = IntStream.range(0, NEW_SIZE).mapToObj(x -> new Card(
            CardClass.CARD,
            UUID.randomUUID().toString(),
            x,
            RecordType.CAR,
            Picture.AIRPLANE,
            UUID.randomUUID().toString(),
            emptyList(),
            "",
            false
        )
    ).toList();

    @DataProvider
    public Object[][] dataProvider() {
        return new Object[][]{
            {CARDS, CARDS, emptyList()},
            {REPLACE, CARDS, emptyList()},
            {CARDS, REPLACE,
                IntStream.range(0, SIZE).mapToObj(
                    x -> new ImportRecord(ImportAction.REPLACE, CARDS.get(x), REPLACE.get(x))
                ).toList()
            },
            {CARDS, ADD,
                IntStream.range(0, NEW_SIZE).mapToObj(
                    x -> new ImportRecord(ImportAction.ADD, null, ADD.get(x))
                ).toList()
            }
        };
    }

    @Test(dataProvider = "dataProvider")
    public void test(List<Card> cards, List<Card> toImport, List<ImportRecord> expected) {
        var actual = ImportUtil.calculateImport(cards, toImport);
        assertEquals(actual, expected);
    }
}
