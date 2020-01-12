/*
 * Copyright (c) 2014, 2020, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.panteleyev.crypto;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Test
public class TestAES {
    private static final String PASSWORD = UUID.randomUUID().toString();
    private static final String TEXT = "This is 1st line of text\nSecond line\nThird line";

    @Test
    public void testAES128EncryptDecrypt() {
        var aes = AES.aes128();
        var enc = aes.encrypt(TEXT, PASSWORD);
        var dec = aes.decrypt(enc, PASSWORD);
        Assert.assertEquals(new String(dec, StandardCharsets.UTF_8), TEXT, "Decoded text is the same as original");
        Assert.assertEquals(aes.decryptString(enc, PASSWORD), TEXT, "Decoded text is the same as original");
    }

    @Test
    public void testAES256EncryptDecrypt() {
        var aes = AES.aes256();
        var enc = aes.encrypt(TEXT, PASSWORD);
        var dec = aes.decrypt(enc, PASSWORD);
        Assert.assertEquals(new String(dec, StandardCharsets.UTF_8), TEXT, "Decoded text is the same as original");
        Assert.assertEquals(aes.decryptString(enc, PASSWORD), TEXT, "Decoded text is the same as original");
    }

    @Test
    public void testAESInputStreamDecrypt() throws Exception {
        AES aes = AES.aes256();

        var enc = aes.encrypt(TEXT, PASSWORD);

        try (var in = new ByteArrayInputStream(enc)) {
            var dec = aes.decrypt(in, PASSWORD);
            Assert.assertEquals(new String(dec, StandardCharsets.UTF_8), TEXT, "Decoded text is the same as original");
        }
    }

    @Test
    public void testAESOutputStreamEncrypt() throws Exception {
        AES aes = AES.aes256();

        try (var out = new ByteArrayOutputStream()) {
            aes.encrypt(TEXT, PASSWORD, out);

            var decrypted = aes.decryptString(out.toByteArray(), PASSWORD);
            Assert.assertEquals(decrypted, TEXT);
        }
    }

    @Test
    public void testNewInstance() {
        var aes128_1 = AES.aes128();
        var aes128_2 = AES.aes128();
        Assert.assertEquals(aes128_1, aes128_2);

        var aes256_1 = AES.aes256();
        var aes256_2 = AES.aes256();
        Assert.assertEquals(aes256_1, aes256_2);

        Assert.assertNotEquals(aes128_1, aes256_1);
    }

    @Test
    public void testKeyGen() {
        var key128 = AES.generate128key(UUID.randomUUID().toString());
        Assert.assertEquals(key128.length, 128 / 8);

        var key256 = AES.generate256key(UUID.randomUUID().toString());
        Assert.assertEquals(key256.length, 256 / 8);
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
            Assert.assertEquals(decrypted, TEXT);
        }
    }
}
