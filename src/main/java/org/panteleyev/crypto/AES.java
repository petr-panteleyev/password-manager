/*
 Copyright © 2020 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * This interface provides methods for AES encryption and decryption.
 */
public interface AES {
    /**
     * Encrypt string.
     *
     * @param str      string
     * @param password password
     * @return encrypted bytes
     */
    default byte[] encrypt(String str, String password) {
        return encrypt(str.getBytes(StandardCharsets.UTF_8), password);
    }

    /**
     * Encrypt byte array.
     *
     * @param src      byte array
     * @param password password
     * @return encrypted bytes
     */
    byte[] encrypt(byte[] src, String password);

    /**
     * Encrypt bytes and write the result into the stream.
     *
     * @param src      bytes to encrypt
     * @param password password
     * @param out      stream
     * @throws IOException in case of error
     */
    void encrypt(byte[] src, String password, OutputStream out) throws IOException;

    /**
     * Encrypt string and write the result into the stream.
     *
     * @param str      string to encrypt
     * @param password password
     * @param out      stream
     * @throws IOException in case of error
     */
    default void encrypt(String str, String password, OutputStream out) throws IOException {
        encrypt(str.getBytes(StandardCharsets.UTF_8), password, out);
    }

    /**
     * Decrypt byte array.
     *
     * @param bytes    bytes to decrypt
     * @param password password to decrypt
     * @return decrypted bytes
     */
    byte[] decrypt(byte[] bytes, String password);

    /**
     * Decrypt string
     *
     * @param bytes    bytes to decrypt
     * @param password password to decrypt
     * @return decrypted bytes
     */
    String decryptString(byte[] bytes, String password);

    /**
     * Decrypt input stream. Stream must be obtained via {@link #getInputStream(InputStream, String)}.
     *
     * @param in       stream to decrypt
     * @param password password to decrypt
     * @return decrypted bytes
     * @throws IOException in case of error
     */
    byte[] decrypt(InputStream in, String password) throws IOException;

    InputStream getInputStream(InputStream in, String password) throws IOException;

    OutputStream getOutputStream(OutputStream out, String password) throws IOException;

    /**
     * Default 256-bit key generator. This implementation uses SHA-256 message
     * digest algorithm.
     *
     * @param password password string
     * @return key bytes
     */
    static byte[] generate256key(String password) {
        return AESImpl.generateKey(password, "SHA-256");
    }

    /**
     * Default 128-bit key generator. This implementation uses MD5 message digest
     * algorithm.
     *
     * @param password password string
     * @return key bytes
     */
    static byte[] generate128key(String password) {
        return AESImpl.generateKey(password, "MD5");
    }

    /**
     * Return AES instance with specified key generation function.
     *
     * @param keyGen key generation function
     * @return in case of error
     */
    static AES aes(Function<String, byte[]> keyGen) {
        return AESImpl.getInstance(keyGen);
    }

    /**
     * Return AES instance with default 256-bit key generation function.
     * Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files
     * for the appropriate JRE must be installed to use 256-bit keys.
     *
     * @return AES instance
     */
    static AES aes256() {
        return aes(AES::generate256key);
    }

    /**
     * Return AES instance with default 128-bit key generation function.
     *
     * @return in case of error
     */
    static AES aes128() {
        return aes(AES::generate128key);
    }
}
