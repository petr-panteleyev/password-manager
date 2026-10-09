// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.otp;

/// TOTP generator result.
///
/// @param code    generated code
/// @param seconds code seconds
public record TotpResult(String code, int seconds) {
}
