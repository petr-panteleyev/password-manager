/*
 * Copyright (c) 2018, 2019, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.Field;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TestCloneable {

    @DataProvider(name="testCloneable")
    public Object[][] testCloneableDataProvider() {
        return new Object[][] {
            { Card.newCard(UUID.randomUUID().toString(), Picture.AMEX, List.of(), UUID.randomUUID().toString())},
            { Card.newCard(UUID.randomUUID().toString(), Picture.GLASSES,
                Arrays.asList(
                    new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                    new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                    new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString())
                ),
                UUID.randomUUID().toString())
            },
            { Card.newNote(UUID.randomUUID().toString(), UUID.randomUUID().toString(), false) },
        };
    }

    @Test(dataProvider="testCloneable")
    public void testCloneable(Card record) throws Exception {
        var clone = new Card(record);

        Assert.assertEquals(clone, record);
        Assert.assertFalse(clone == record);

        if (record.isCard()) {

        }

    }
}
