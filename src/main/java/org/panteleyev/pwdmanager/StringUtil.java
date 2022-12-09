/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import java.time.LocalDate;

public final class StringUtil {
    private StringUtil() {
    }

    /**
     * Parses {@link LocalDate} object from long value represented as {@link String}.
     *
     * @param value long value
     * @return local date
     */
    public static LocalDate parseLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDate.now();
        }
        try {
            var longValue = Long.parseLong(value);
            return LocalDate.ofEpochDay(longValue);
        } catch (NumberFormatException ex) {
            return LocalDate.now();
        }
    }
}
