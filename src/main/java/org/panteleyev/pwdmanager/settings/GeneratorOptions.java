/*
 Copyright © 2020-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.settings;

import org.panteleyev.commons.password.PasswordCharacterSet;

import java.util.EnumSet;
import java.util.Set;

/**
 * This class defines options for generated password.
 */
public record GeneratorOptions(
        boolean upperCase,
        boolean lowerCase,
        boolean digits,
        boolean symbols,
        int length
) {
    public Set<PasswordCharacterSet> getPasswordCharacterSets() {
        var result = EnumSet.noneOf(PasswordCharacterSet.class);
        if (upperCase) {
            result.add(PasswordCharacterSet.UPPER_CASE_LETTERS);
        }
        if (lowerCase) {
            result.add(PasswordCharacterSet.LOWER_CASE_LETTERS);
        }
        if (digits) {
            result.add(PasswordCharacterSet.DIGITS);
        }
        if (symbols) {
            result.add(PasswordCharacterSet.SYMBOLS);
        }
        return result;
    }
}
