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

import javafx.scene.input.DataFormat
import java.util.UUID

/** Base interface for all types of cards */
internal interface Record {
    val id: String
    val modified: Long
    val type: RecordType
    val picture: Picture
    val name: String

    fun cloneWithNewId() : Record

    companion object {
        const val MIME_TYPE = "application/x-org-panteleyev-password-manager-record-id"
        val DATA_FORMAT = DataFormat(MIME_TYPE)

        fun newId() = UUID.randomUUID().toString()
    }
}

/** Folder */
internal data class Category (override val id: String = Record.newId(),
                              override val modified: Long = System.currentTimeMillis(),
                              override val name: String,
                              override val type: RecordType = RecordType.EMPTY,
                              override val picture: Picture,
                              val expanded: Boolean = false
) : Record {
    override fun cloneWithNewId(): Record = this.copy(id = Record.newId())
}

/** Generic card with fields */
internal data class Card(override val id: String = Record.newId(),
                         override val modified: Long = System.currentTimeMillis(),
                         override val name: String,
                         override val picture: Picture,
                         val fields: List<Field>,
                         val note: String = ""
) : Record {
    override val type: RecordType = RecordType.EMPTY

    override fun cloneWithNewId(): Record = this.copy(id = Record.newId())
}

/** Link to another card */
internal data class Link(override val id: String = Record.newId(),
                         val targetId: String
) : Record {
    override val modified: Long = System.currentTimeMillis()
    override val type: RecordType = RecordType.EMPTY
    override val picture: Picture = Picture.FOLDER
    override val name: String = ""

    override fun cloneWithNewId(): Record = this.copy(id = Record.newId())
}

/** Special kind of card for full text note */
internal data class Note (override val id: String = Record.newId(),
                          override val modified: Long = System.currentTimeMillis(),
                          override val name: String,
                          val text: String
) : Record {
    override val type: RecordType = RecordType.EMPTY
    override val picture: Picture = Picture.NOTE

    override fun cloneWithNewId(): Record = this.copy(id = Record.newId())
}