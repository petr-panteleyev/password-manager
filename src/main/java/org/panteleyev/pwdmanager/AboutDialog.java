/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.pwdmanager.model.Picture;

import java.time.LocalDate;

import static org.panteleyev.fx.BoxFactory.vBox;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.LabelFactory.label;
import static org.panteleyev.pwdmanager.Constants.APP_TITLE;
import static org.panteleyev.pwdmanager.Constants.BUILD_INFO_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Styles.BIG_SPACING;
import static org.panteleyev.pwdmanager.Styles.SMALL_SPACING;
import static org.panteleyev.pwdmanager.Styles.STYLE_ABOUT_LABEL;

final class AboutDialog extends BaseDialog<Object> {
    private static final String YEAR = Integer.toString(LocalDate.now().getYear());

    private static final String RUNTIME = System.getProperty("java.vm.version") + " " + System.getProperty("os.arch");
    private static final String VM = System.getProperty("java.vm.name") + " by " + System.getProperty("java.vm.vendor");

    AboutDialog(Controller owner) {
        super(owner, settings().getAboutDialogCssFileUrl());

        setHeaderText(APP_TITLE);
        setGraphic(new ImageView(Picture.WALLET.getBigImage()));

        setTitle("About " + APP_TITLE);

        var aboutLabel = label(APP_TITLE + " " + fxString(BUILD_INFO_BUNDLE, "version"));
        aboutLabel.getStyleClass().add(STYLE_ABOUT_LABEL);

        var vBox = vBox(BIG_SPACING,
                vBox(SMALL_SPACING,
                        aboutLabel,
                        label("Built on " + fxString(BUILD_INFO_BUNDLE, "timestamp"))
                ),
                vBox(SMALL_SPACING,
                        label("Runtime version: " + RUNTIME),
                        label("VM: " + VM)
                ),
                vBox(SMALL_SPACING,
                        label("Copyright © 2017-" + YEAR + " Petr Panteleyev")
                )
        );

        getDialogPane().setContent(vBox);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK);
    }
}
