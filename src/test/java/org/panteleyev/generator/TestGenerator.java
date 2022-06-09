/*
 Copyright © 2020-2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.generator;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@Test
public class TestGenerator {

    @DataProvider
    public Object[][] dataProvider() {
        return new Object[][]{
                {new GeneratorOptions(true, true, true, true, 32)},
                {new GeneratorOptions(true, true, true, true, 32)},
                {new GeneratorOptions(false, false, true, false, 4)},
                {new GeneratorOptions(false, false, true, false, 4)},
        };
    }

    @Test(dataProvider = "dataProvider")
    public void testGenerator(GeneratorOptions options) {
        var password = new Generator().generate(options);
        assertEquals(password.length(), options.length());

        assertEquals(contains(password, Generator.UPPER_CASE_CHARS), options.upperCase());
        assertEquals(contains(password, Generator.LOWER_CASE_CHARS), options.lowerCase());
        assertEquals(contains(password, Generator.DIGITS), options.digits());
        assertEquals(contains(password, Generator.SYMBOLS), options.symbols());
        assertFalse(contains(password, Generator.BAD_LETTERS));
    }

    private static boolean contains(String password, List<Character> characters) {
        for (var i = 0; i < password.length(); i++) {
            var ch = password.charAt(i);
            if (characters.contains(ch)) {
                return true;
            }
        }
        return false;
    }
}
