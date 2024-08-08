/*
 Copyright © 2024 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev;

import java.util.UUID;

public final class TestUtil {
    public static String randomString() {
        return UUID.randomUUID().toString();
    }

    private TestUtil() {
    }
}
