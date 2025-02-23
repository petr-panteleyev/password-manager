/*
 Copyright © 2025 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyCombination;
import org.controlsfx.control.action.Action;

import java.util.function.Consumer;

public final class ActionFactory {
    private ActionFactory() {
    }

    public static Action action(String text, KeyCombination accelerator, Consumer<ActionEvent> eventHandler) {
        return action(text, accelerator, eventHandler, false);
    }

    public static Action action(String text, Consumer<ActionEvent> eventHandler, boolean disabled) {
        var action = new Action(text, eventHandler);
        action.setDisabled(disabled);
        return action;
    }

    public static Action action(
            String text,
            KeyCombination accelerator,
            Consumer<ActionEvent> eventHandler,
            boolean disabled
    ) {
        var action = new Action(text, eventHandler);
        action.setAccelerator(accelerator);
        action.setDisabled(disabled);
        return action;
    }

    public static Action action(
            String text,
            KeyCombination accelerator,
            Consumer<ActionEvent> eventHandler,
            boolean disabled,
            Consumer<Action> builder
    ) {
        var action = new Action(text, eventHandler);
        action.setAccelerator(accelerator);
        action.setDisabled(disabled);
        builder.accept(action);
        return action;
    }
}
