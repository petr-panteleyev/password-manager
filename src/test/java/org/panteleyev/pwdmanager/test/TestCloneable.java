/*
 * Copyright (c) 2016, 2017, Petr Panteleyev <petr@panteleyev.org>
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
package org.panteleyev.pwdmanager.test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import org.panteleyev.pwdmanager.Card;
import org.panteleyev.pwdmanager.Category;
import org.panteleyev.pwdmanager.Field;
import org.panteleyev.pwdmanager.FieldType;
import org.panteleyev.pwdmanager.Note;
import org.panteleyev.pwdmanager.Picture;
import org.panteleyev.pwdmanager.Record;
import org.panteleyev.pwdmanager.RecordType;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestCloneable {

    @DataProvider(name="testCloneable")
    public Object[][] testCloneableDataProvider() {
        return new Object[][] {
            { new Card(UUID.randomUUID().toString(), Picture.AMEX, Collections.EMPTY_LIST, UUID.randomUUID().toString())},
            { new Card(UUID.randomUUID().toString(), Picture.GLASSES,
                Arrays.asList(
                    new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                    new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString()),
                    new Field(FieldType.STRING, UUID.randomUUID().toString(), UUID.randomUUID().toString())
                ),
                UUID.randomUUID().toString())
            },
            { new Category(UUID.randomUUID().toString(), RecordType.CREDIT_CARD, Picture.AMEX) },
            { new Note(UUID.randomUUID().toString(), UUID.randomUUID().toString()) },
        };
    }

    @Test(dataProvider="testCloneable")
    public void testCloneable(Record record) throws Exception {
        Object clone = record.clone();

        Assert.assertEquals(clone, record);
        Assert.assertFalse(clone == record);

        if (record instanceof Card) {

        }

    }
}
