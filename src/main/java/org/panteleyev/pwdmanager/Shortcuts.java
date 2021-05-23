/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

interface Shortcuts {
    KeyCodeCombination SHORTCUT_C = new KeyCodeCombination(KeyCode.C, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_D = new KeyCodeCombination(KeyCode.D, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_F = new KeyCodeCombination(KeyCode.F, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_G = new KeyCodeCombination(KeyCode.G, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_I = new KeyCodeCombination(KeyCode.I, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_N = new KeyCodeCombination(KeyCode.N, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_O = new KeyCodeCombination(KeyCode.O, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_P = new KeyCodeCombination(KeyCode.P, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_T = new KeyCodeCombination(KeyCode.T, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_U = new KeyCodeCombination(KeyCode.U, SHORTCUT_DOWN);
    KeyCodeCombination SHORTCUT_V = new KeyCodeCombination(KeyCode.V, SHORTCUT_DOWN);
    KeyCodeCombination SHIFT_DELETE = new KeyCodeCombination(KeyCode.DELETE, SHIFT_DOWN);

    KeyCodeCombination DELETE = new KeyCodeCombination(KeyCode.DELETE);
}
