/*
 Copyright © 2020-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.generator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GeneratorTest {

    private static List<Arguments> dataProvider() {
        return List.of(
                Arguments.of(new GeneratorOptions(true, true, true, true, 32)),
                Arguments.of(new GeneratorOptions(true, true, true, true, 32)),
                Arguments.of(new GeneratorOptions(false, false, true, false, 4)),
                Arguments.of(new GeneratorOptions(false, false, true, false, 4))
        );
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void testGenerator(GeneratorOptions options) {
        var password = new Generator().generate(options);
        assertEquals(options.length(), password.length());

        assertEquals(options.upperCase(), contains(password, Generator.UPPER_CASE_CHARS));
        assertEquals(options.lowerCase(), contains(password, Generator.LOWER_CASE_CHARS));
        assertEquals(options.digits(), contains(password, Generator.DIGITS));
        assertEquals(options.symbols(), contains(password, Generator.SYMBOLS));
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
