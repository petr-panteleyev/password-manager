/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import java.time.LocalDate;

public interface StringUtil {
    /**
     * Parses {@link LocalDate} object from long value represented as {@link String}.
     *
     * @param value long value
     * @return local date
     */
    static LocalDate parseLocalDate(String value) {
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
