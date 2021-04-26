/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import org.panteleyev.pwdmanager.model.Card;
import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.model.ImportRecord;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface ImportUtil {

    static List<ImportRecord> calculateImport(Collection<Card> existing, Collection<Card> toImport) {
        var result = new ArrayList<ImportRecord>();
        for (var card : toImport) {
            existing.stream()
                .filter(x -> x.uuid().equals(card.uuid()))
                .findAny()
                .ifPresentOrElse(
                    found -> {
                        if (found.modified() < card.modified()) {
                            result.add(new ImportRecord(ImportAction.REPLACE, found, card));
                        }
                    },
                    () -> result.add(new ImportRecord(ImportAction.ADD, null, card))
                );
        }
        return result;
    }
}
