/*
 Copyright © 2017-2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.panteleyev.pwdmanager.cells.PictureListCell;

import java.util.Arrays;
import java.util.Comparator;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("unused")
public enum Picture {
    AIRPLANE,
    AMAZON,
    AMEX,
    APPLE,
    AUTO,
    BANK,
    BITBUCKET,
    CD,
    CHESS,
    CHROME,
    CITI,
    COMPUTER,
    CREDIT_CARD,
    DINERS,
    EDUCATION,
    EMAIL,
    FACEBOOK,
    FEMALE,
    FLICKR,
    FOLDER,
    GENERIC,
    GLASSES,
    GITHUB,
    GPLUS,
    HOUSE,
    ICQ,
    IMPORTANT,
    INFO,
    INSURANCE,
    INTERNET,
    IPHONE,
    JAVA,
    JCB,
    LINKEDIN,
    MALE,
    MASTERCARD,
    MEDICINE,
    MIR,
    MOBILE,
    MOZILLA,
    NOTE,
    NVIDIA,
    ODNOKLASSNIKI,
    ORIGIN,
    PASSPORT,
    PAYPAL,
    PHONE,
    RAIFFEISEN,
    REDDIT,
    RUSSIA,
    SBERBANK,
    SHOP,
    SKYPE,
    STEAM,
    TELEGRAM,
    TRAIN,
    TWITTER,
    UNIONPAY,
    US,
    VISA,
    VK,
    WALLET,
    WIFI,
    WOW,
    YAHOO,
    YANDEX;

    public static final int SMALL_IMAGE_SIZE = 24;
    public static final int BIG_IMAGE_SIZE = 48;

    private final Image image;
    private final Image bigImage;

    Picture() {
        var res = name().toLowerCase() + ".png";
        var bigRes = name().toLowerCase() + "-48.png";

        image = new Image(requireNonNull(getClass().getResourceAsStream("/images/" + res)));
        bigImage = new Image(requireNonNull(getClass().getResourceAsStream("/images/" + bigRes)));
    }

    public Image getImage() {
        return image;
    }

    public Image getBigImage() {
        return bigImage;
    }

    public static void setupComboBox(ComboBox<Picture> comboBox) {
        comboBox.setCellFactory(p -> new PictureListCell());
        comboBox.setButtonCell(new PictureListCell());
        comboBox.setItems(FXCollections.observableArrayList(
                Arrays.stream(Picture.values()).sorted(Comparator.comparing(Enum::name)).toList())
        );
    }

    public static Picture of(String stringValue) {
        if (stringValue == null || stringValue.isBlank()) {
            return GENERIC;
        }

        try {
            return valueOf(stringValue.toUpperCase());
        } catch (Exception ex) {
            return GENERIC;
        }
    }

    public static ImageView imageView(Image image, int width, int height) {
        var view = new ImageView(image);
        view.setFitWidth(width);
        view.setFitHeight(height);
        return view;
    }
}
