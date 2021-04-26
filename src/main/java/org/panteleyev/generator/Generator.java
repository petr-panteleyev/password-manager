/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.generator;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Generator {
    static final List<Character> UPPER_CASE_CHARS = List.of(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    );

    static final List<Character> LOWER_CASE_CHARS = List.of(
        'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    );

    static final List<Character> DIGITS = List.of(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    );

    static final List<Character> SYMBOLS = List.of(
        '@', '#', '$', '%', '&', '*', '(', ')', '-', '+', '=', '^', '.', ','
    );

    static final List<Character> BAD_LETTERS = List.of(
        'I', 'l', 'O', '0'
    );

    private static record Bucket(List<Character> chars, boolean used) {
        char getChar(int index) {
            return chars.get(index);
        }

        int getSize() {
            return chars.size();
        }

        boolean check(String pwd) {
            for (var ch : pwd.toCharArray()) {
                if (chars.contains(ch)) {
                    return true;
                }
            }
            return false;
        }
    }

    private final Random random = new Random(System.currentTimeMillis());

    /**
     * Generates new password.
     *
     * @throws IllegalArgumentException if password length &lt; 4
     */
    public String generate(GeneratorOptions options) {
        int len = options.length();

        if (len < 4) {
            throw new IllegalArgumentException("Password length must be 4 or greater");
        }

        var usedBuckets = List.of(
            new Bucket(UPPER_CASE_CHARS, options.upperCase()),
            new Bucket(LOWER_CASE_CHARS, options.lowerCase()),
            new Bucket(DIGITS, options.digits()),
            new Bucket(SYMBOLS, options.symbols())
        ).stream()
            .filter(Bucket::used)
            .toList();

        if (usedBuckets.isEmpty()) {
            throw new IllegalArgumentException("At least one character set must be selected");
        }

        var password = "";
        while (password.isEmpty()) {
            var res = new StringBuilder();

            for (int i = 0; i < len; ++i) {
                var bucket = usedBuckets.get(random.nextInt(usedBuckets.size()));

                char sym = ' ';
                var symOk = false;
                while (!symOk) {
                    sym = bucket.getChar(random.nextInt(bucket.getSize()));
                    symOk = !BAD_LETTERS.contains(sym);
                }
                res.append(sym);
            }

            var pwd = res.toString();

            if (usedBuckets.stream().allMatch(x -> x.check(pwd))) {
                password = pwd;
            }
        }

        return password;
    }
}
