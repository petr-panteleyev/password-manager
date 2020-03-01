package org.panteleyev.pwdmanager;

/*
 * Copyright (c) Petr Panteleyev. All rights reserved.
 * Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import org.panteleyev.pwdmanager.model.Field;

final class FieldWrapper extends Field {
    private boolean show;

    FieldWrapper(Field that) {
        super(that);
    }

    void toggleShow() {
        show = !show;
    }

    boolean getShow() {
        return show;
    }
}
