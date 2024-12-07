/*
 Copyright © 2022-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class StringUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        } catch (NumberFormatException _) {
            try {
                return LocalDate.parse(value, DATE_FORMATTER);
            } catch (DateTimeParseException _) {
                return LocalDate.now();
            }
        }
    }
}
