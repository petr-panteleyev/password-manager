/*
 Copyright © 2020-2021 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.generator;

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
}
