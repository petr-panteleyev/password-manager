/*
 * Copyright (c) 2016, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.panteleyev.pwdmanager;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class CategoryDialog extends RecordDialog<Category> {

    public CategoryDialog(Category category) {
        setTitle("Category");
        initControls(category);

        GridPane pane = new GridPane();
        pane.setHgap(5);
        pane.setVgap(5);

        pane.add(new Label("Name:"), 1, 1);
        pane.add(nameEdit, 2, 1);
        pane.add(new Label("Default Type:"), 1, 2);
        pane.add(typeList, 2, 2);
        pane.add(new Label("Icon:"), 1, 3);
        pane.add(pictureList, 2, 3);

        getDialogPane().setContent(pane);

        setResultConverter((ButtonType b) -> {
            if (b == ButtonType.OK) {
                return new Category(
                    nameEdit.getText(),
                    typeList.getSelectionModel().getSelectedItem(),
                    pictureList.getSelectionModel().getSelectedItem()
                );
            } else {
                return null;
            }
        });
    }

    private void initControls(Category category) {
        initLists();

        if (category != null) {
            nameEdit.setText(category.getName());
            typeList.getSelectionModel().select(category.getType());
            pictureList.getSelectionModel().select(category.getPicture());
        } else {
            nameEdit.setText("");
            typeList.getSelectionModel().select(RecordType.PASSWORD);
            pictureList.getSelectionModel().select(Picture.FOLDER);
        }
    }
}
