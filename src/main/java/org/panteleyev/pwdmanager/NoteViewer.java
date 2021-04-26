/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import static org.panteleyev.fx.MenuFactory.menuItem;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_C;

public class NoteViewer extends TextArea {
    public NoteViewer() {
        setEditable(false);
        setContextMenu(createContextMenu());
        setFocusTraversable(false);
    }

    private ContextMenu createContextMenu() {
        return new ContextMenu(
            menuItem("Copy...", SHORTCUT_C, x -> copy())
        );
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
