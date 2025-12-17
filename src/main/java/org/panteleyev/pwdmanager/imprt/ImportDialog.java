// Copyright © 2021-2025 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.imprt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import org.panteleyev.fx.BaseDialog;
import org.panteleyev.fx.Controller;
import org.panteleyev.fx.factories.TableFactory;
import org.panteleyev.pwdmanager.model.ImportAction;
import org.panteleyev.pwdmanager.model.ImportRecord;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.functional.Scope.apply;
import static org.panteleyev.fx.factories.MenuFactory.checkMenuItem;
import static org.panteleyev.fx.factories.StringFactory.string;
import static org.panteleyev.fx.factories.TableFactory.tableStringColumn;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.GlobalContext.settings;
import static org.panteleyev.pwdmanager.Shortcuts.SHORTCUT_P;
import static org.panteleyev.pwdmanager.Styles.STYLE_ACTION_ADD;
import static org.panteleyev.pwdmanager.Styles.STYLE_ACTION_DELETE;
import static org.panteleyev.pwdmanager.Styles.STYLE_ACTION_REPLACE;
import static org.panteleyev.pwdmanager.Styles.STYLE_ACTION_RESTORE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_ACTION;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_IMPORT;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_SKIP;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_TITLE;
import static org.panteleyev.pwdmanager.bundles.Internationalization.I18N_UPDATED;

public final class ImportDialog extends BaseDialog<List<ImportRecord>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    private static final Map<ImportAction, String> STYLE_MAP = Map.of(
            ImportAction.ADD, STYLE_ACTION_ADD,
            ImportAction.REPLACE, STYLE_ACTION_REPLACE,
            ImportAction.DELETE, STYLE_ACTION_DELETE,
            ImportAction.RESTORE, STYLE_ACTION_RESTORE
    );

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

            getStyleClass().removeAll(STYLE_MAP.values());
            if (importRecord == null || empty || !importRecord.approved()) {
                return;
            }

            getStyleClass().add(STYLE_MAP.get(importRecord.getEffectiveAction()));
        }
    }

    public ImportDialog(Controller owner, List<ImportRecord> importRecords) {
        super(owner, settings().getDialogCssFileUrl());
        this.importRecords.addAll(importRecords);

        setTitle(UI_BUNDLE.getString(I18N_IMPORT));
        setResizable(true);

        tableView.setPrefSize(1024, 768);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setContextMenu(createContextMenu());

        var w = tableView.widthProperty().subtract(20);

        tableView.setRowFactory(_ -> new ImportRow());

        tableView.getColumns().setAll(List.of(
                apply(tableStringColumn(string(UI_BUNDLE, I18N_TITLE)), c -> {
                    c.valueConverter(r -> r.cardToImport().name());
                    c.widthBinding(w.multiply(0.35));
                }),
                apply(TableFactory.<ImportRecord, Long>tableValueColumn(string(UI_BUNDLE, I18N_UPDATED)), c -> {
                    c.setCellFactory(_ -> new TimestampCell());
                    c.valueConverter(r -> r.cardToImport().modified());
                    c.widthBinding(w.multiply(0.35));
                }),
                apply(TableFactory.<ImportRecord, ImportAction>tableValueColumn(string(UI_BUNDLE, I18N_ACTION)),
                        c -> {
                            c.valueConverter(ImportRecord::getEffectiveAction);
                            c.widthBinding(w.multiply(0.35));
                        })
        ));

        setResultConverter(buttonType -> {
            if (OK.equals(buttonType)) {
                return importRecords.stream()
                        .filter(ImportRecord::approved)
                        .toList();
            } else {
                return Collections.emptyList();
            }
        });

        getDialogPane().setContent(tableView);
        getDialogPane().getButtonTypes().addAll(OK, CANCEL);
    }

    private Optional<ImportRecord> getSelectedItem() {
        return Optional.ofNullable(tableView.getSelectionModel().getSelectedItem());
    }

    private void onToggleApproval() {
        getSelectedItem().ifPresent(item -> {
            var newItem = item.toggleApproval();
            var index = importRecords.indexOf(item);
            importRecords.set(index, newItem);
            tableView.getSelectionModel().select(index);
        });
        var column = tableView.getColumns().getFirst();
        column.setVisible(false);
        column.setVisible(true);
    }

    private ContextMenu createContextMenu() {
        var toggleMenuItem = apply(checkMenuItem(string(UI_BUNDLE, I18N_SKIP)), item -> {
            item.setAccelerator(SHORTCUT_P);
            item.setOnAction(_ -> onToggleApproval());
            item.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        });

        var menu = new ContextMenu(
                toggleMenuItem
        );

        menu.setOnShowing(_ -> toggleMenuItem.setSelected(
                getSelectedItem().map(r -> !r.approved()).orElse(false))
        );

        return menu;
    }
}
