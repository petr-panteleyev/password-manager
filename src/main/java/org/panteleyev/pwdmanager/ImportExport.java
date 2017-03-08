/*
 * Copyright (c) 2016, 2017, Petr Panteleyev <petr@panteleyev.org>
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
package org.panteleyev.pwdmanager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TreeItem;

class ImportExport {
    private static TreeItem<Record> catItem = null;

    private static String cardName = null;
    private static List<Field> fields = null;
    private static StringBuilder note = null;
    private static StringBuilder cardNotes = null;
    private static boolean endOfSection = true;

    private static void finalizeCard() {
        if (cardName != null) {
            Record record;
            if (note != null) {
                record = new Note(cardName, note.toString());
                note = null;
            } else {
                String cNote = (cardNotes == null)? "" : cardNotes.toString();
                record = new Card(cardName, Picture.PASSWORD, fields, cNote);
                cardNotes = null;
            }

            catItem.getChildren().add(new TreeItem<>(record));
            cardName = null;
            endOfSection = false;
        }
    }

    private static void parseField(String line) {
        int colon = line.indexOf(':');
        if (colon != -1) {
            String fName = line.substring(0, colon);
            String fValue = line.substring(colon + 1).trim();
            Field field = new Field(FieldType.STRING, fName, fValue);
            fields.add(field);
        } else {
            int space = line.indexOf(' ');
            if (space != -1) {
                String fName = line.substring(0, space);
                String fValue = line.substring(space + 1).trim();
                Field field = new Field(FieldType.STRING, fName, fValue);
                fields.add(field);
            }
        }
    }

    public static TreeItem<Record> eWalletImport(File file) {
        TreeItem<Record> root = new TreeItem<>(new Category("root", RecordType.EMPTY, Picture.FOLDER));

        try {
            Files.lines(file.toPath()).map(String::trim).forEach(line -> {
                if (endOfSection && line.startsWith("Category")) {
                    finalizeCard();

                    int colon = line.indexOf(':');
                    String name = line.substring(colon + 1).trim();
                    Category cat = new Category(name, RecordType.EMPTY, Picture.FOLDER);
                    catItem = new TreeItem<>(cat);
                    root.getChildren().add(catItem);
                    endOfSection = false;
                    return;
                }

                if (endOfSection && line.startsWith("Card")) {
                    finalizeCard();

                    cardName = line.substring(5);
                    fields = new ArrayList<>();
                    endOfSection = false;
                    return;
                }

                if (line.startsWith("Card Notes")) {
                    cardNotes = new StringBuilder();
                    return;
                }

                if (line.startsWith("Text")) {
                    note = new StringBuilder();
                    int space = line.indexOf(' ');
                    if (space != -1) {
                        note.append(line.substring(space + 1).trim()).append("\n");
                    }
                    return;
                }

                if (note != null) {
                    note.append(line).append("\n");
                    endOfSection = line.isEmpty();
                    return;
                }

                if (cardNotes != null) {
                    cardNotes.append(line).append("\n");
                    endOfSection = line.isEmpty();
                    return;
                }

                if (line.isEmpty()) {
                    endOfSection = true;
                    return;
                } else {
                    endOfSection = false;
                }

                if (cardName != null) {
                    parseField(line);
                }
            });

            return root;
        } catch (IOException ex) {
            return null;
        }
    }
}
