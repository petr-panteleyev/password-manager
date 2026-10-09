// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.otp;

import org.apache.commons.codec.binary.Base32;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.Objects;

/**
 * Generates Time-Based One-Time Passwords (TOTP) as defined in RFC 6238. This implementation produces 6-digit codes
 * using HMAC-SHA1 and 30-second time steps.
 */
public final class TotpGenerator {

    private static final int TIME_STEP_SECONDS = 30;
    private static final int CODE_DIGITS = 6;

    private static final byte[] EMPTY_BYTES = new byte[0];

    /**
     * Generates a TOTP code from the provided shared secret.
     *
     * @param secret the shared secret (Base32 encoded)
     * @return a 6-digit TOTP code
     */
    public static TotpResult generate(String secret) {
        secret = Objects.requireNonNull(secret, "Secret must not be null").toUpperCase();

        var key = decodeBase32(secret);

        // Get current Unix timestamp and calculate time step
        var seconds = Instant.now().getEpochSecond();
        var currentTimeStep = Instant.now().getEpochSecond() / TIME_STEP_SECONDS;

        // Convert time step to 8-byte array (big endian)
        var timeBytes = ByteBuffer.allocate(8)
                .order(ByteOrder.BIG_ENDIAN)
                .putLong(currentTimeStep)
                .array();

        try {
            // Calculate HMAC-SHA1
            var mac = Mac.getInstance("HmacSHA1");
            var keySpec = new SecretKeySpec(key, "HmacSHA1");
            mac.init(keySpec);
            var hash = mac.doFinal(timeBytes);

            // Dynamic truncation to get 4-byte code
            var offset = hash[hash.length - 1] & 0xf;
            var binary = ((hash[offset] & 0x7f) << 24)
                    | ((hash[offset + 1] & 0xff) << 16)
                    | ((hash[offset + 2] & 0xff) << 8)
                    | (hash[offset + 3] & 0xff);

            // Generate code with desired number of digits
            var mod = (int) Math.pow(10, CODE_DIGITS);
            var code = binary % mod;

            // Format with leading zeros
            return new TotpResult(String.format("%06d", code), (int) seconds);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate TOTP code", e);
        }
    }

    // RFC 4648 Base32 decoding using Apache Commons Codec
    static byte[] decodeBase32(String encoded) {
        if (encoded == null) {
            throw new NullPointerException("Base32 encoded string cannot be null");
        }

        if (encoded.isEmpty()) return EMPTY_BYTES;

        // Validate Base32 characters (A-Z, 2-7, =)
        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            if (!((c >= 'A' && c <= 'Z') || (c >= '2' && c <= '7') || c == '=')) {
                throw new IllegalArgumentException("Invalid Base32 character: " + c);
            }
        }

        // Create Base32 decoder and decode the string
        return new Base32().decode(encoded);
    }
}