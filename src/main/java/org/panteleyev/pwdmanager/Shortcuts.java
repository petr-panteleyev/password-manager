/*
 Copyright © 2020-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

import static javafx.scene.input.KeyCombination.ALT_DOWN;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;

final class Shortcuts {
    public static final KeyCodeCombination SHORTCUT_C = new KeyCodeCombination(KeyCode.C, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_D = new KeyCodeCombination(KeyCode.D, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_F = new KeyCodeCombination(KeyCode.F, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_G = new KeyCodeCombination(KeyCode.G, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_I = new KeyCodeCombination(KeyCode.I, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_N = new KeyCodeCombination(KeyCode.N, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_O = new KeyCodeCombination(KeyCode.O, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_P = new KeyCodeCombination(KeyCode.P, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_T = new KeyCodeCombination(KeyCode.T, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_U = new KeyCodeCombination(KeyCode.U, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_V = new KeyCodeCombination(KeyCode.V, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHIFT_DELETE = new KeyCodeCombination(KeyCode.DELETE, SHIFT_DOWN);
    public static final KeyCodeCombination SHORTCUT_ALT_S = new KeyCodeCombination(KeyCode.S, SHORTCUT_DOWN, ALT_DOWN);

    public static final KeyCodeCombination DELETE = new KeyCodeCombination(KeyCode.DELETE);

    private Shortcuts() {
    }
}
