/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.crypto;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

class AESImpl implements AES {
    private static final String CLASS_NAME = AESImpl.class.getName();

    private static final String ALGO = "AES";
    private static final String ALGO_FULL = ALGO + "/CBC/PKCS5Padding";
    private static final int    IV_LENGTH = 16;

    private static final int    FILE_BUF_SIZE = 4096;

    private static final Map<Function<String,byte[]>,AES> IMPLS = new ConcurrentHashMap<>(2);

    private final Function<String,byte[]> keyGen;

    static byte[] generateKey(String password, String algo) {
        try {
            var md = MessageDigest.getInstance(algo);
            return md.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            return null;
        }
    }

    static AES getInstance(Function<String, byte[]> keyGen) {
        Objects.requireNonNull(keyGen);
        return IMPLS.computeIfAbsent(keyGen, k -> new AESImpl(keyGen));
    }

    private static byte[] generateIV() {
        var res = new byte[IV_LENGTH];
        var sr = new SecureRandom();
        sr.nextBytes(res);
        return res;
    }

    private AESImpl(Function<String,byte[]> keyGen) {
        this.keyGen = keyGen;
    }

    @Override
    public byte[] encrypt(byte[] src, String password) {
        try {
            var iv = generateIV();
            var cipher = getCipher(Cipher.ENCRYPT_MODE, password, iv);
            var encrypted = cipher.doFinal(src);
            var res = Arrays.copyOf(iv, iv.length + encrypted.length);
            for (int i = 0, j = iv.length; i < encrypted.length; i++, j++) {
                res[j] = encrypted[i];
            }
            return res;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public void encrypt(byte[] src, String password, OutputStream out) throws IOException {
        try (var cOut = getOutputStream(out, password)) {
            cOut.write(src);
        }
    }

    @Override
    public byte[] decrypt(byte[] bytes, String password) {
        if (bytes.length <= IV_LENGTH) {
            throw new IllegalArgumentException("Byte array to decrypt is too short");
        }

        try {
            var c = getCipher(Cipher.DECRYPT_MODE, password, Arrays.copyOf(bytes, IV_LENGTH));
            return c.doFinal(bytes, IV_LENGTH, bytes.length - IV_LENGTH);
        } catch (Exception ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public String decryptString(byte[] bytes, String password) {
        var res = decrypt(bytes, password);
        return new String(res, StandardCharsets.UTF_8);
    }

    @Override
    public byte[] decrypt(InputStream in, String password) throws IOException {
        if (in instanceof CipherInputStream) {
            throw new IllegalArgumentException("CipherInputStream must be used directly");
        }

        try (var out = new ByteArrayOutputStream()) {
            try (var cin = getInputStream(in, password)) {
                var buf = new byte[FILE_BUF_SIZE];
                int nRead;
                while ((nRead = cin.read(buf)) > 0) {
                    for (int i = 0; i < nRead; i++) {
                        out.write(buf[i]);
                    }
                }
            }

            return out.toByteArray();
        }
    }

    @Override
    public InputStream getInputStream(InputStream in, String password) throws IOException {
        try {
            var iv = new byte[IV_LENGTH];
            for (int i = 0; i < IV_LENGTH; i++) {
                int b = in.read();
                if (b == -1) {
                    throw new IOException("premature end of stream");
                }
                iv[i] = (byte)b;
            }

            return new CipherInputStream(in, getCipher(Cipher.DECRYPT_MODE, password, iv));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException  | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }

    @Override
    public OutputStream getOutputStream(OutputStream out, String password) throws IOException {
        try {
            var iv = generateIV();
            var cipher = getCipher(Cipher.ENCRYPT_MODE, password, iv);
            out.write(iv);
            return new CipherOutputStream(out, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(CLASS_NAME).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }

    private Cipher getCipher(int opMode, String password, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException  {
        var cipher = Cipher.getInstance(ALGO_FULL);
        var key = new SecretKeySpec(keyGen.apply(password), ALGO);
        cipher.init(opMode, key, new IvParameterSpec(iv));
        return cipher;
    }
}
