package org.panteleyev.pwdmanager.model;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

public enum CardClass {
    CARD("Card"),
    NOTE("Note");

    private final String className;

    CardClass(String className) {
        this.className = className;
    }

    public String toString() {
        return className;
    }
}
