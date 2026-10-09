// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.otp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TotpGeneratorTest {

    @Test
    void testGenerateWithNullSecret() {
        assertThrows(NullPointerException.class, () -> TotpGenerator.generate(null));
    }

    @Test
    void testGenerateWithEmptySecret() {
        assertThrows(RuntimeException.class, () -> TotpGenerator.generate(""));
    }

    @Test
    void testGenerateWithInvalidBase32Characters() {
        assertThrows(IllegalArgumentException.class, () -> TotpGenerator.generate("INVALID_CHARACTERS!"));
    }

    @Test
    void testGenerateReturnsSixDigitCode() {
        // Test with a valid Base32 secret
        var secret = "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ"; // 20 bytes of zeros in Base32
        var result = TotpGenerator.generate(secret);

        // Verify the code is 6 digits
        assertEquals(6, result.code().length());
        assertTrue(result.code().matches("\\d{6}"));
    }

    @Test
    void testGenerateWithDifferentSecrets() {
        // Test with several different valid Base32 secrets
        var secrets = new String[]{
                "JBSWY3DPEHPK3PXP", // 16 characters
                "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ", // 32 characters
                "IFBEGRCFIZDUQSKKJBWGIZLTEB2FI2BILJ2GQZJCMFZGSZLOEBUW42LE" // 56 characters
        };

        for (var secret : secrets) {
            var result = TotpGenerator.generate(secret);
            assertEquals(6, result.code().length());
            assertTrue(result.code().matches("\\d{6}"));
        }
    }
}