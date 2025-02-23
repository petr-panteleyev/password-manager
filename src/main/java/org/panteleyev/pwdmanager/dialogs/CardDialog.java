/*
 Copyright © 2017-2025 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.dialogs;

import javafx.application.Platform;
import org.panteleyev.fx.Controller;
import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.RecordType;

import static java.util.Objects.requireNonNull;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.FxUtils.COLON;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_CARD;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TYPE;

public final class CardDialog extends RecordDialog {
    private final RecordType defaultType;

    public CardDialog(Controller owner, RecordType defaultType) {
        super(owner);
        this.defaultType = requireNonNull(defaultType);
        initialize();
    }

    private void initialize() {
        setTitle(fxString(UI_BUNDLE, I18N_CARD));
        createDefaultButtons(UI_BUNDLE);

        initLists();
        setTypeLabelText(fxString(UI_BUNDLE, I18N_TYPE, COLON));

        getNameEdit().setText("");
        getTypeList().getSelectionModel().select(defaultType);
        getPictureList().getSelectionModel().select(RecordType.PASSWORD.getPicture());

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                var type = getTypeList().getSelectionModel().getSelectedItem();
                return new Card(
                        getNameEdit().getText(),
                        getPictureList().getSelectionModel().getSelectedItem(),
                        type.getFieldSet()
                );
            } else {
                return null;
            }
        });

        Platform.runLater(this::setupValidator);
        Platform.runLater(getNameEdit()::requestFocus);
    }
}
