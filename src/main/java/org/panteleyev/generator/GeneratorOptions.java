/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
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
