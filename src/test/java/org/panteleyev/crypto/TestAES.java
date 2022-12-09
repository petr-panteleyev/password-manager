/*
 Copyright © 2020-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.crypto;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TestAES {
    private static final String PASSWORD = UUID.randomUUID().toString();
    private static final String TEXT = "This is 1st line of text\nSecond line\nThird line";

    @Test
    public void testAES128EncryptDecrypt() {
        var aes = AES.aes128();
        var enc = aes.encrypt(TEXT, PASSWORD);
        var dec = aes.decrypt(enc, PASSWORD);
        assertEquals(TEXT, new String(dec, StandardCharsets.UTF_8), "Decoded text is the same as original");
        assertEquals(TEXT, aes.decryptString(enc, PASSWORD), "Decoded text is the same as original");
    }

    @Test
    public void testAES256EncryptDecrypt() {
        var aes = AES.aes256();
        var enc = aes.encrypt(TEXT, PASSWORD);
        var dec = aes.decrypt(enc, PASSWORD);
        assertEquals(TEXT, new String(dec, StandardCharsets.UTF_8), "Decoded text is the same as original");
        assertEquals(TEXT, aes.decryptString(enc, PASSWORD), "Decoded text is the same as original");
    }

    @Test
    public void testAESInputStreamDecrypt() throws Exception {
        var aes = AES.aes256();
        var enc = aes.encrypt(TEXT, PASSWORD);

        try (var in = new ByteArrayInputStream(enc)) {
            var dec = aes.decrypt(in, PASSWORD);
            assertEquals(TEXT, new String(dec, StandardCharsets.UTF_8), "Decoded text is the same as original");
        }
    }

    @Test
    public void testAESOutputStreamEncrypt() throws Exception {
        var aes = AES.aes256();

        try (var out = new ByteArrayOutputStream()) {
            aes.encrypt(TEXT, PASSWORD, out);

            var decrypted = aes.decryptString(out.toByteArray(), PASSWORD);
            assertEquals(TEXT, decrypted);
        }
    }

    @Test
    public void testNewInstance() {
        var aes128_1 = AES.aes128();
        var aes128_2 = AES.aes128();
        assertEquals(aes128_1, aes128_2);

        var aes256_1 = AES.aes256();
        var aes256_2 = AES.aes256();
        assertEquals(aes256_1, aes256_2);

        assertNotEquals(aes128_1, aes256_1);
    }

    @Test
    public void testKeyGen() {
        var key128 = AES.generate128key(UUID.randomUUID().toString());
        assertEquals(128 / 8, key128.length);

        var key256 = AES.generate256key(UUID.randomUUID().toString());
        assertEquals(256 / 8, key256.length);
    }

    @Test
    public void testAESgetOutputStream() throws Exception {
        var aes256 = AES.aes256();

        try (var out = new ByteArrayOutputStream()) {
            try (var cout = aes256.getOutputStream(out, PASSWORD)) {
                cout.write(TEXT.getBytes(StandardCharsets.UTF_8));
            }

            var encrypted = out.toByteArray();
            var decrypted = aes256.decryptString(encrypted, PASSWORD);
            assertEquals(TEXT, decrypted);
        }
    }
}
