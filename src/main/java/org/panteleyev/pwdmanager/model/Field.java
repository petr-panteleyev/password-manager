// Copyright © 2017-2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.pwdmanager.StringUtil.parseLocalDate;

public record Field(FieldType type, String name, Object value) {
    private static final DateTimeFormatter GENERIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter EXPIRATION_MONTH_FORMATTER = DateTimeFormatter.ofPattern("MM/yy");

    @SuppressWarnings("DataFlowIssue")
    public Field {
        type = requireNonNull(type, "Type must not be null");
        name = requireNonNull(name, "Field name must not be null");
        value = requireNonNull(value, "Field value must not be null");
    }

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
