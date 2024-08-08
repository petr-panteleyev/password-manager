/*
 Copyright © 2017-2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.panteleyev.pwdmanager.StringUtil.parseLocalDate;

public record Field(FieldType type, String name, Object value) {
    private static final DateTimeFormatter GENERIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter EXPIRATION_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    public String getValueAsString() {
        return switch (type) {
            case DATE -> GENERIC_DATE_FORMATTER.format((LocalDate) value);
            case EXPIRATION_MONTH -> EXPIRATION_MONTH_FORMATTER.format((LocalDate) value);
            case CARD_TYPE -> ((CardType) value).getName();
            default -> value.toString();
        };
    }

    public boolean isEmpty() {
        return getValueAsString().isEmpty();
    }

    public static Object deserializeValue(FieldType type, String value) {
        return switch (type) {
            case CARD_TYPE -> CardType.of(value);
            case DATE, EXPIRATION_MONTH -> parseLocalDate(value);
            default -> value;
        };
    }
}
