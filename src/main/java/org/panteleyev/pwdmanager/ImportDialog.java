/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package org.panteleyev.pwdmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Map;
import java.util.Optional;
import static javafx.scene.control.ButtonType.CANCEL;
import static javafx.scene.control.ButtonType.OK;
import static org.panteleyev.fx.FxUtils.fxString;
import static org.panteleyev.fx.MenuFactory.checkMenuItem;
import static org.panteleyev.fx.TableColumnBuilder.tableColumn;
import static org.panteleyev.pwdmanager.Constants.UI_BUNDLE;
import static org.panteleyev.pwdmanager.Options.options;
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

final class ImportDialog extends BaseDialog<List<ImportRecord>> {
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
        super(owner, options().getDialogCssFileUrl());
        this.importRecords.addAll(importRecords);

        setTitle(UI_BUNDLE.getString(I18N_IMPORT));
        setResizable(true);

        tableView.setPrefSize(1024, 768);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setContextMenu(createContextMenu());

        var w = tableView.widthProperty().subtract(20);

        tableView.setRowFactory(t -> new ImportRow());

        tableView.getColumns().setAll(List.of(
            tableColumn(fxString(UI_BUNDLE, I18N_TITLE), b ->
                b.withPropertyCallback(r -> r.cardToImport().name())
                    .withWidthBinding(w.multiply(0.35))
            ),
            tableColumn(fxString(UI_BUNDLE, I18N_UPDATED), (TableColumnBuilder<ImportRecord, Long> b) ->
                b.withPropertyCallback(r -> r.cardToImport().modified())
                    .withCellFactory(x -> new TimestampCell())
                    .withWidthBinding(w.multiply(0.35))
            ),
            tableColumn(fxString(UI_BUNDLE, I18N_ACTION),
                b -> b.withPropertyCallback(ImportRecord::getEffectiveAction)
                    .withWidthBinding(w.multiply(0.27))
            )
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
        var column = tableView.getColumns().get(0);
        column.setVisible(false);
        column.setVisible(true);
    }

    private ContextMenu createContextMenu() {
        var toggleMenuItem = checkMenuItem(fxString(UI_BUNDLE, I18N_SKIP), false,
            SHORTCUT_P, event -> onToggleApproval());
        toggleMenuItem.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());

        var menu = new ContextMenu(
            toggleMenuItem
        );

        menu.setOnShowing(e -> toggleMenuItem.setSelected(
            getSelectedItem().map(r -> !r.approved()).orElse(false))
        );

        return menu;
    }
}
