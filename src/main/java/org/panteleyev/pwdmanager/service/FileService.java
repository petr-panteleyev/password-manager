// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.service;

import org.panteleyev.crypto.AES;
import org.panteleyev.pwdmanager.Serializer;
import org.panteleyev.pwdmanager.model.WalletRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

/// Service responsible for loading and saving password wallet files.
///
/// Encapsulates all file I/O operations including encryption, decryption, and XML serialization/deserialization.
/// Provides structured error handling via [FileServiceException].
///
/// This class is thread-safe for concurrent read operations. Write operations should be serialized by the caller.
public final class FileService {
    private static final Logger LOGGER = Logger.getLogger(FileService.class.getName());

    /// Loads wallet records from the specified file.
    ///
    /// If the password is empty, the file is read as unencrypted XML. Otherwise, the file is decrypted using AES-256
    /// before parsing.
    ///
    /// @param file     the file to load from
    /// @param password the decryption password, or empty string for unencrypted files
    /// @return the list of wallet records
    /// @throws FileServiceException if the file cannot be read or decrypted
    public List<WalletRecord> loadDocument(File file, String password) throws FileServiceException {
        requireNonNull(file, "file must not be null");

        if (!file.exists()) {
            throw new FileServiceException(
                    FileServiceException.Reason.FILE_NOT_FOUND, file.getAbsolutePath(), "File not found", null
            );
        }

        try (var fileInputStream = new FileInputStream(file);
             var decryptedStream = decrypt(fileInputStream, password)
        ) {
            return Serializer.deserialize(decryptedStream);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load file: " + file.getAbsolutePath(), e);
            throw new FileServiceException(
                    FileServiceException.Reason.DECRYPTION_ERROR, file.getAbsolutePath(),
                    "Unable to read or decrypt the file. The file may be corrupted or the password may be incorrect.",
                    e
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load file: " + file.getAbsolutePath(), e);
            throw new FileServiceException(
                    FileServiceException.Reason.SERIALIZATION_ERROR,
                    file.getAbsolutePath(),
                    "Unable to parse the file. The file may be corrupted or in an unsupported format.",
                    e
            );
        }
    }

    /// Saves wallet records to the specified file.
    ///
    /// If the password is empty, the file is written as unencrypted XML. Otherwise, the data is encrypted using AES-256
    /// before writing.
    ///
    /// @param file     the file to save to
    /// @param password the encryption password, or empty string for unencrypted files
    /// @param records  the list of wallet records to save
    /// @throws FileServiceException if the file cannot be written or encrypted
    public void writeDocument(File file, String password, List<WalletRecord> records) throws FileServiceException {
        requireNonNull(file, "file must not be null");
        requireNonNull(records, "records must not be null");

        try (var bOut = new ByteArrayOutputStream()) {
            encrypt(bOut, password, records);

            try (var fOut = new FileOutputStream(file)) {
                fOut.write(bOut.toByteArray());
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to write file: " + file.getAbsolutePath(), e);
            throw new FileServiceException(
                    FileServiceException.Reason.ENCRYPTION_ERROR,
                    file.getAbsolutePath(),
                    "Unable to save the file. Check disk space and file permissions.",
                    e
            );
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to write file: " + file.getAbsolutePath(), e);
            throw new FileServiceException(
                    FileServiceException.Reason.SERIALIZATION_ERROR,
                    file.getAbsolutePath(),
                    "Unable to serialize the data.",
                    e
            );
        }
    }

    /// Decrypts an input stream using the given password.
    ///
    /// @param inputStream the encrypted input stream
    /// @param password    the decryption password, or empty string for unencrypted streams
    /// @return a stream containing the decrypted data
    /// @throws IOException if decryption fails
    public InputStream decrypt(InputStream inputStream, String password) throws IOException {
        try (var outputStream = new ByteArrayOutputStream()) {
            if (password != null && !password.isEmpty()) {
                try (var cin = AES.aes256().getInputStream(inputStream, password)) {
                    cin.transferTo(outputStream);
                }
            } else {
                inputStream.transferTo(outputStream);
            }
            return new ByteArrayInputStream(outputStream.toByteArray());
        }
    }

    /// Encrypts and serializes records to an output stream.
    ///
    /// @param outputStream the output stream to write to
    /// @param password     the encryption password, or empty string for unencrypted output
    /// @param records      the records to serialize
    /// @throws IOException if encryption or serialization fails
    private void encrypt(OutputStream outputStream, String password, List<WalletRecord> records) throws IOException {
        if (password != null && !password.isEmpty()) {
            try (var cOut = AES.aes256().getOutputStream(outputStream, password)) {
                Serializer.serialize(cOut, records);
            }
        } else {
            Serializer.serialize(outputStream, records);
        }
    }
}
