// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.service;

/**
 * Exception thrown by {@link FileService} operations.
 *
 * <p>Provides structured error information for file I/O failures,
 * including the affected file path and a user-friendly message.</p>
 */
public class FileServiceException extends Exception {

    /** Enumerated error reasons. */
    public enum Reason {
        /** The specified file does not exist. */
        FILE_NOT_FOUND,
        /** Decryption failed (wrong password or corrupted data). */
        DECRYPTION_ERROR,
        /** Encryption failed. */
        ENCRYPTION_ERROR,
        /** XML serialization/deserialization failed. */
        SERIALIZATION_ERROR,
        /** An unknown error occurred. */
        UNKNOWN
    }

    private final Reason reason;
    private final String userMessage;

    /**
     * Creates a new exception with the given reason, file path, and cause.
     *
     * @param reason   the error reason
     * @param filePath the affected file path, or {@code null}
     * @param message  a user-friendly message
     * @param cause    the underlying cause, or {@code null}
     */
    public FileServiceException(Reason reason, String filePath, String message, Throwable cause) {
        super(message, cause);
        this.reason = reason;
        this.userMessage = buildUserMessage(reason, filePath, message);
    }

    /**
     * Returns the structured error reason.
     *
     * @return the error reason
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * Returns a user-friendly error message suitable for display.
     *
     * @return the user message
     */
    public String getUserMessage() {
        return userMessage;
    }

    private static String buildUserMessage(Reason reason, String filePath, String detail) {
        var sb = new StringBuilder();
        if (filePath != null) {
            sb.append(filePath).append(": ");
        }
        sb.append(detail);
        return sb.toString();
    }
}
