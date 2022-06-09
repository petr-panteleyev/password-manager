/*
 Copyright © 2022 Petr Panteleyev <petr@panteleyev.org>
 SPDX-License-Identifier: BSD-2-Clause
 */
package org.panteleyev.pwdmanager.model;

import javafx.scene.image.Image;

public enum CardType {
    AMEX("American Express", Picture.AMEX),
    DINERS("Diners Club", Picture.DINERS),
    JCB("JCB", Picture.JCB),
    MASTERCARD("MasterCard", Picture.MASTERCARD),
    MIR("Мир", Picture.MIR),
    PAYPAL("PayPal", Picture.PAYPAL),
    UNION_PAY("Union Pay", Picture.UNIONPAY),
    VISA("VISA", Picture.VISA),
    OTHER("Other", Picture.CREDIT_CARD);
    private final String name;
    private final Picture picture;

    CardType(String name, Picture picture) {
        this.name = name;
        this.picture = picture;
    }

    public String getName() {
        return name;
    }

    public Picture getPicture() {
        return picture;
    }

    public Image getImage() {
        return picture.getImage();
    }

    public static CardType of(String value) {
        try {
            return CardType.valueOf(value);
        } catch (Exception ex) {
            return VISA;
        }
    }
}
