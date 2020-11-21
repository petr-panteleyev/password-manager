/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class NoteViewer extends TextArea {
    public NoteViewer() {
        setEditable(false);
        setContextMenu(createContextMenu());
        setFocusTraversable(false);
    }

    private ContextMenu createContextMenu() {
        var menu = new ContextMenu();

        var m1 = new MenuItem("Copy...");
        m1.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));
        m1.setOnAction(x -> copy());

        menu.getItems().addAll(m1);
        return menu;
    }

    @Override
    public void copy() {
        var text = getSelectedText();
        if (text.isEmpty()) {
            text = getText();
        }

        if (!text.isEmpty()) {
            var clipboard = Clipboard.getSystemClipboard();
            var ct = new ClipboardContent();
            ct.putString(text);
            clipboard.setContent(ct);
        }
    }
}
