/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.TableColumnBuilder;
import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.model.ImportRecord;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.MenuFactory.checkMenuItem;
import static org.panteleyev.fx.TableColumnBuilder.tableColumn;
import static org.panteleyev.pwdmanager.Constants.RB;
import static org.panteleyev.pwdmanager.Options.options;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_P;
import static org.panteleyev.pwdmanager.Styles.STYLE_ACTION_ADD;
import static org.panteleyev.pwdmanager.Styles.STYLE_ACTION_REPLACE;

public class ImportDialog extends BaseDialog<List<ImportRecord>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final String ACTION_ADD = fxString(RB, "Add");
    private static final String ACTION_REPLACE = fxString(RB, "Replace");
    private static final String ACTION_SKIP = fxString(RB, "Skip");

    private final ObservableList<ImportRecord> importRecords = FXCollections.observableArrayList();
    private final TableView<ImportRecord> tableView = new TableView<>(importRecords);

    private static class TimestampCell extends TableCell<ImportRecord, Long> {
        @Override
        protected void updateItem(Long timestamp, boolean empty) {
            super.updateItem(timestamp, empty);
            if (timestamp == null || empty) {
                setText("");
            } else {
                var dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
                setText(FORMATTER.format(dateTime));
            }
        }
    }

    private static class ImportRow extends TableRow<ImportRecord> {
        @Override
        protected void updateItem(ImportRecord importRecord, boolean empty) {
            super.updateItem(importRecord, empty);

            getStyleClass().removeAll(STYLE_ACTION_ADD);
            if (importRecord == null || empty || !importRecord.isApproved()) {
                return;
            }

            getStyleClass().add(
                switch (importRecord.getAction()) {
                    case ADD -> STYLE_ACTION_ADD;
                    case REPLACE -> STYLE_ACTION_REPLACE;
                }
            );
        }
    }

    public ImportDialog(Controller owner, List<ImportRecord> importRecords) {
        super(owner, options().getDialogCssFileUrl());
        this.importRecords.addAll(importRecords);

        setTitle(RB.getString("Import"));
        setResizable(true);

        tableView.setPrefSize(1024, 768);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setContextMenu(createContextMenu());

        var w = tableView.widthProperty().subtract(20);

        tableView.setRowFactory(t -> new ImportRow());

        tableView.getColumns().setAll(List.of(
            tableColumn(fxString(RB, "Name"), b ->
                b.withPropertyCallback(r -> r.getCardToImport().name())
                    .withWidthBinding(w.multiply(0.35))
            ),
            tableColumn(fxString(RB, "Updated"), (TableColumnBuilder<ImportRecord, Long> b) ->
                b.withPropertyCallback(r -> r.getCardToImport().modified())
                    .withCellFactory(x -> new TimestampCell())
                    .withWidthBinding(w.multiply(0.35))
            ),
            tableColumn(fxString(RB, "Action"), b ->
                b.withPropertyCallback(r -> !r.isApproved() ?
                    ACTION_SKIP : r.getAction() == ImportAction.ADD ? ACTION_ADD : ACTION_REPLACE
                ).withWidthBinding(w.multiply(0.27))
            )
        ));

        setResultConverter(buttonType -> {
            if (buttonType.equals(ButtonType.OK)) {
                return importRecords.stream()
                    .filter(ImportRecord::isApproved)
                    .toList();
            } else {
                return Collections.emptyList();
            }
        });

        getDialogPane().setContent(tableView);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    }

    private Optional<ImportRecord> getSelectedItem() {
        return Optional.ofNullable(tableView.getSelectionModel().getSelectedItem());
    }

    private void onToggleApproval() {
        getSelectedItem().ifPresent(ImportRecord::toggleApproval);
        var column = tableView.getColumns().get(0);
        column.setVisible(false);
        column.setVisible(true);
    }

    private ContextMenu createContextMenu() {
        var toggleMenuItem = checkMenuItem(fxString(RB, "Skip"), false, SHORTCUT_P, event -> onToggleApproval());
        toggleMenuItem.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        var menu = new ContextMenu(
            toggleMenuItem
        );

        menu.setOnShowing(e -> toggleMenuItem.setSelected(
            getSelectedItem().map(r -> !r.isApproved()).orElse(false))
        );

        return menu;
    }
}
