/*
 * Copyright (c) 2017, Petr Panteleyev <petr@panteleyev.org>
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

package org.panteleyev.pwdmanager

import javafx.scene.image.Image
import java.util.ResourceBundle

internal enum class RecordType constructor(val picture: Picture, val fieldSet: List<Field>) {
    EMPTY(Picture.GENERIC, emptyList()),
    CREDIT_CARD(Picture.CREDIT_CARD, listOf(
            Field(FieldType.STRING, "Card Provider", ""),
            Field(FieldType.STRING, "Credit Card Type", ""),
            Field(FieldType.CREDIT_CARD_NUMBER, "Card Number", ""),
            Field(FieldType.STRING, "Expiration Date", ""),
            Field(FieldType.HIDDEN, "PIN", ""),
            Field(FieldType.STRING, "Name on Card", ""),
            Field(FieldType.STRING, "Phone Number", ""),
            Field(FieldType.STRING, "CVC#", ""),
            Field(FieldType.STRING, "User Name", ""),
            Field(FieldType.HIDDEN, "Password", "")
    )),
    CAR(Picture.AUTO, listOf(
            Field(FieldType.STRING, "Title", ""),
            Field(FieldType.STRING, "VIN", ""),
            Field(FieldType.STRING, "Passport", ""),
            Field(FieldType.STRING, "Registration", ""),
            Field(FieldType.STRING, "Engine", ""),
            Field(FieldType.STRING, "Body", ""),
            Field(FieldType.STRING, "Plate", "")
    )),
    GLASSES(Picture.GLASSES, listOf(
            Field(FieldType.STRING, "Right (O.D.) SPH", ""),
            Field(FieldType.STRING, "Right CYL", ""),
            Field(FieldType.HIDDEN, "Right AXIS", ""),
            Field(FieldType.STRING, "Left (O.S.) SPH", ""),
            Field(FieldType.STRING, "Left CYL", ""),
            Field(FieldType.STRING, "Left AXIS", ""),
            Field(FieldType.STRING, "Pupil Distance (mm)", ""),
            Field(FieldType.STRING, "Doctor's Name", ""),
            Field(FieldType.STRING, "Doctor's Phone #", ""),
            Field(FieldType.STRING, "Other Information", "")
    )),
    PASSPORT(Picture.PASSPORT, listOf(
            Field(FieldType.STRING, "Title", ""),
            Field(FieldType.STRING, "Number", ""),
            Field(FieldType.STRING, "Issued", ""),
            Field(FieldType.STRING, "Valid Until", ""),
            Field(FieldType.STRING, "Issuer", "")
    )),
    EMAIL(Picture.EMAIL, listOf(
            Field(FieldType.STRING, "System", ""),
            Field(FieldType.EMAIL, "E-Mail Address", ""),
            Field(FieldType.STRING, "User Name", ""),
            Field(FieldType.HIDDEN, "Password", ""),
            Field(FieldType.STRING, "IMAP", ""),
            Field(FieldType.STRING, "IMAP Port", ""),
            Field(FieldType.STRING, "SMTP", ""),
            Field(FieldType.STRING, "SMTP Port", "")
    )),
    PASSWORD(Picture.GENERIC, listOf(
            Field(FieldType.STRING, "System", ""),
            Field(FieldType.STRING, "User Name", ""),
            Field(FieldType.HIDDEN, "Password", ""),
            Field(FieldType.LINK, "URL", "")
    ));

    val typeName: String

    val image: Image
        get() = picture.image

    init {
        val bundle = ResourceBundle.getBundle("RecordType")
        typeName = bundle.getString(name)
    }
}
