/*
 Copyright © 2025 Petr Panteleyev
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import java.io.File;

public record StartupParameters(File initialFile, String password, boolean saveFileName) {
}
