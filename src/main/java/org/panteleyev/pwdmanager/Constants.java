/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import org.panteleyev.pwdmanager.model.Card;
import java.util.Comparator;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;
import static javafx.scene.input.KeyCombination.SHIFT_DOWN;
import static javafx.scene.input.KeyCombination.SHORTCUT_DOWN;
import static org.panteleyev.fx.FxUtils.fxString;

public final class Constants {
    public static final ResourceBundle RB = getBundle("org.panteleyev.pwdmanager.ui");
    public static final ResourceBundle BUILD_INFO_BUNDLE = getBundle("org.panteleyev.pwdmanager.buildInfo");
    public static final ResourceBundle FIELD_TYPE_BUNDLE = getBundle("org.panteleyev.pwdmanager.FieldType");

    public static final String APP_TITLE = fxString(RB, "Application Title");

    // Shortcuts
    public static final KeyCodeCombination SHORTCUT_C = new KeyCodeCombination(KeyCode.C, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_D = new KeyCodeCombination(KeyCode.D, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_F = new KeyCodeCombination(KeyCode.F, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_I = new KeyCodeCombination(KeyCode.I, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_N = new KeyCodeCombination(KeyCode.N, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_O = new KeyCodeCombination(KeyCode.O, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_T = new KeyCodeCombination(KeyCode.T, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHORTCUT_V = new KeyCodeCombination(KeyCode.V, SHORTCUT_DOWN);
    public static final KeyCodeCombination SHIFT_DELETE = new KeyCodeCombination(KeyCode.DELETE, SHIFT_DOWN);

    // Styles
    public static final double BIG_SPACING = 10.0;
    public static final double SMALL_SPACING = 5.0;
    public static final String STYLE_GRID_PANE = "gridPane";
    public static final String STYLE_CARD_CONTENT_TITLE = "cardContentTitle";
    public static final String STYLE_ABOUT_LABEL = "aboutLabel";

    // Comparators
    public static final Comparator<Card> COMPARE_CARDS_BY_NAME = Comparator.comparing(Card::name);
    public static final Comparator<Card> COMPARE_CARDS_BY_FAVORITE = Comparator.comparing(Card::favorite).reversed();

    private Constants() {
    }
}
