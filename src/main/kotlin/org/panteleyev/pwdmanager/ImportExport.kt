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

import javafx.scene.control.TreeItem
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.ArrayList

internal object ImportExport {
    private var catItem: TreeItem<Record>? = null

    private var cardName: String? = null
    private var fields: MutableList<Field>? = null
    private var note: StringBuilder? = null
    private var cardNotes: StringBuilder? = null
    private var endOfSection = true

    private fun finalizeCard() {
        if (cardName != null) {
            val record: Record
            if (note != null) {
                record = Note(name = cardName!!, text = note!!.toString())
                note = null
            } else {
                val cNote = if (cardNotes == null) "" else cardNotes!!.toString()
                record = Card(name = cardName!!, picture = Picture.PASSWORD, fields = fields!!, note = cNote)
                cardNotes = null
            }

            catItem!!.children.add(TreeItem(record))
            cardName = null
            endOfSection = false
        }
    }

    private fun parseField(line: String) {
        val colon = line.indexOf(':')
        if (colon != -1) {
            val fName = line.substring(0, colon)
            val fValue = line.substring(colon + 1).trim { it <= ' ' }
            val field = Field(FieldType.STRING, fName, fValue)
            fields!!.add(field)
        } else {
            val space = line.indexOf(' ')
            if (space != -1) {
                val fName = line.substring(0, space)
                val fValue = line.substring(space + 1).trim { it <= ' ' }
                val field = Field(FieldType.STRING, fName, fValue)
                fields!!.add(field)
            }
        }
    }

    fun eWalletImport(file: File): TreeItem<Record>? {
        val root = TreeItem<Record>(Category(name = "root", picture = Picture.FOLDER))

        try {
            Files.lines(file.toPath()).map<String>( { it.trim({ it <= ' ' }) }).forEach { line ->
                if (endOfSection && line.startsWith("Category")) {
                    finalizeCard()

                    val colon = line.indexOf(':')
                    val name = line.substring(colon + 1).trim { it <= ' ' }
                    val cat = Category(name = name, picture = Picture.FOLDER)
                    catItem = TreeItem<Record>(cat)
                    root.children.add(catItem)
                    endOfSection = false
                    return@forEach
                }

                if (endOfSection && line.startsWith("Card")) {
                    finalizeCard()

                    cardName = line.substring(5)
                    fields = ArrayList<Field>()
                    endOfSection = false
                    return@forEach
                }

                if (line.startsWith("Card Notes")) {
                    cardNotes = StringBuilder()
                    return@forEach
                }

                if (line.startsWith("Text")) {
                    note = StringBuilder()
                    val space = line.indexOf(' ')
                    if (space != -1) {
                        note!!.append(line.substring(space + 1).trim { it <= ' ' }).append("\n")
                    }
                    return@forEach
                }

                if (note != null) {
                    note!!.append(line).append("\n")
                    endOfSection = line.isEmpty()
                    return@forEach
                }

                if (cardNotes != null) {
                    cardNotes!!.append(line).append("\n")
                    endOfSection = line.isEmpty()
                    return@forEach
                }

                if (line.isEmpty()) {
                    endOfSection = true
                    return@forEach
                } else {
                    endOfSection = false
                }

                if (cardName != null) {
                    parseField(line)
                }
            }

            return root
        } catch (ex: IOException) {
            return null
        }

    }
}
