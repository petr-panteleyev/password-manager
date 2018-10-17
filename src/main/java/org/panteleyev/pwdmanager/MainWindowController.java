/*
 * Copyright (c) 2016, 2018, Petr Panteleyev <petr@panteleyev.org>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
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

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.panteleyev.crypto.AES;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class MainWindowController extends BorderPane implements Styles {
    private final ResourceBundle rb = PasswordManagerApplication.getBundle();

    static final String CSS_PATH = "/org/panteleyev/pwdmanager/PasswordManager.css";

    private static final String PREF_CURRENT_FILE = "currentFile";

    static final String CSS_DROP_TARGET = "dropTarget";
    static final String CSS_DROP_BELOW = "dropBelow";
    static final String CSS_DROP_ABOVE = "dropAbove";

    static final String[] CSS_DND_STYLES = {CSS_DROP_TARGET, CSS_DROP_BELOW, CSS_DROP_ABOVE};

    // TreeView clipboard
    private boolean cut = false;

    private final TreeView<Record> cardTreeView = new TreeView<>();
    private final BorderPane leftPane = new BorderPane(cardTreeView);
    private final TitledPane treeViewPane = new TitledPane("", leftPane);

    private final MenuItem ctxNewCardMenuItem = new MenuItem(rb.getString("menu.edit.newCard"));
    private final MenuItem ctxNewNoteMenuItem = new MenuItem(rb.getString("menu.edit.newNote"));
    private final MenuItem ctxNewCategoryMenuItem = new MenuItem(rb.getString("menu.edit.newCategory"));
    private final MenuItem ctxCutMenuItem = new MenuItem(rb.getString("menu.edit.cut"));
    private final MenuItem ctxDeleteMenuItem = new MenuItem(rb.getString("menu.edit.delete"));
    private final MenuItem ctxCardPasteMenuItem = new MenuItem(rb.getString("menu.edit.paste"));
    private final MenuItem ctxCardPasteLinkMenuItem = new MenuItem(rb.getString("menu.edit.pasteLink"));

    private TextField searchTextField;
    private final Label cardContentTitleLabel = new Label();
    private final Button cardEditButton = new Button("Edit...");
    private final BorderPane recordViewPane = new BorderPane();

    // Menu items
    private final MenuItem newCardMenuItem = new MenuItem(rb.getString("menu.edit.newCard"));
    private final MenuItem newCategoryMenuItem = new MenuItem(rb.getString("menu.edit.newCategory"));
    private final MenuItem newNoteMenuItem = new MenuItem(rb.getString("menu.edit.newNote"));
    private final MenuItem changePasswordMenuItem = new MenuItem(rb.getString("menu.tools.changePassword"));
    private final MenuItem deleteMenuItem = new MenuItem(rb.getString("menu.edit.delete"));

    private final NoteViewer noteViewer = new NoteViewer();
    private final CardViewer cardContentView = new CardViewer();

    private Record rootRecord;
    private TreeItem<Record> rootTreeItem;       // store root item for full tree

    private final SimpleObjectProperty<File> currentFile = new SimpleObjectProperty<>();
    private String currentPassword;

    private final Preferences preferences = Preferences.userNodeForPackage(MainWindowController.class);

    private static MainWindowController mainWindowController;

    public MainWindowController() {
        createMainMenu();
        createControls();
        createCardTreeContextMenu();
        initialize();
        setupScrolling();
    }

    private void createMainMenu() {
        // File
        var fileNewMenuItem = new MenuItem(rb.getString("menu.file.new"));
        fileNewMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        fileNewMenuItem.setOnAction(a -> onNewFile());

        var fileOpenMenuitem = new MenuItem(rb.getString("menu.file.open"));
        fileOpenMenuitem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        fileOpenMenuitem.setOnAction(a -> onOpenFile());

        var fileExitMenuItem = new MenuItem(rb.getString("menu.file.exit"));
        fileExitMenuItem.setOnAction(a -> onExit());

        var fileMenu = new Menu(rb.getString("menu.file"), null,
                fileNewMenuItem, fileOpenMenuitem, new SeparatorMenuItem(), fileExitMenuItem);

        // Edit
        newCategoryMenuItem.setOnAction(a -> onNewCategory());
        newCategoryMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN));

        newCardMenuItem.setOnAction(a -> onNewCard());
        newNoteMenuItem.setOnAction(a -> onNewNote());

        var editMenu = new Menu(rb.getString("menu.edit"), null,
                newCategoryMenuItem, new SeparatorMenuItem(),
                newCardMenuItem, newNoteMenuItem, new SeparatorMenuItem(),
                deleteMenuItem);

        // Tools
        changePasswordMenuItem.setOnAction(a -> onChangePassword());
        var toolsMenu = new Menu(rb.getString("menu.tools"), null, changePasswordMenuItem);

        // Help
        var helpAboutMenuItem = new MenuItem(rb.getString("menu.help.about"));
        helpAboutMenuItem.setOnAction(a -> onAbout());
        var helpMenu = new Menu(rb.getString("menu.help"), null, helpAboutMenuItem);

        var menuBar = new MenuBar(fileMenu, editMenu, toolsMenu, helpMenu);
        menuBar.setUseSystemMenuBar(true);

        setTop(menuBar);
    }

    private void createControls() {
        cardTreeView.setShowRoot(false);
        BorderPane.setAlignment(cardTreeView, Pos.CENTER);

        cardEditButton.setOnAction(a -> onEditCard());

        var buttonBar = new ButtonBar();
        buttonBar.getButtons().setAll(cardEditButton);

        recordViewPane.setTop(cardContentTitleLabel);
        recordViewPane.setBottom(buttonBar);

        BorderPane.setMargin(buttonBar, new Insets(5, 5, 5, 5));

        cardContentTitleLabel.getStyleClass().add(CARD_CONTENT_TITLE);
        BorderPane.setAlignment(cardContentTitleLabel, Pos.CENTER);
        BorderPane.setAlignment(buttonBar, Pos.CENTER);

        treeViewPane.setMaxHeight(Double.MAX_VALUE);
        treeViewPane.setMaxWidth(Double.MAX_VALUE);

        var split = new SplitPane(treeViewPane, recordViewPane);
        split.setDividerPositions(0.30);

        setCenter(split);
    }

    private void createCardTreeContextMenu() {
        ctxNewCategoryMenuItem.setOnAction(a -> onNewCategory());
        ctxNewCardMenuItem.setOnAction(a -> onNewCard());
        ctxNewNoteMenuItem.setOnAction(a -> onNewNote());
        ctxDeleteMenuItem.setOnAction(a -> onDeleteRecord());
        ctxCutMenuItem.setOnAction(a -> onCardCut());
        ctxCardPasteMenuItem.setOnAction(a -> onCardPaste());
        ctxCardPasteLinkMenuItem.setOnAction(a -> onCardPasteLink());

        var copyMenuItem = new MenuItem(rb.getString("menu.edit.copy"));
        copyMenuItem.setOnAction(a -> onCardCopy());

        var menu = new ContextMenu(
                ctxNewCategoryMenuItem,
                new SeparatorMenuItem(),
                ctxNewCardMenuItem,
                ctxNewNoteMenuItem,
                new SeparatorMenuItem(),
                ctxDeleteMenuItem,
                new SeparatorMenuItem(),
                ctxCutMenuItem,
                copyMenuItem,
                ctxCardPasteMenuItem,
                ctxCardPasteLinkMenuItem
        );

        menu.setOnShowing(e -> onCardTreeContextMenuShowing());

        cardTreeView.setContextMenu(menu);
    }

    private void initialize() {
        mainWindowController = this;

        cardTreeView.setCellFactory((TreeView<Record> p) -> new CardTreeViewCell(this));
        cardTreeView.setShowRoot(false);
        cardTreeView.getSelectionModel().selectedItemProperty().addListener(x -> onTreeViewSelected());

        cardEditButton.disableProperty().bind(cardTreeView.getSelectionModel().selectedItemProperty().isNull());

        currentFile.addListener((ObservableValue<? extends File> observable, File oldValue, File newValue) -> {
            treeViewPane.setText(newValue != null ? newValue.getName() : "");
        });

        searchTextField = new TextField();
        searchTextField.textProperty().addListener((x, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                doSearch(newValue);
            }
        });
        leftPane.setTop(searchTextField);
        BorderPane.setMargin(searchTextField, new Insets(0, 0, 10, 0));

        // Cmd parameter overrides stored file but does not overwrite the setting.
        var params = PasswordManagerApplication.getApplication().getParameters();
        var fileName = params.getNamed().get("file");
        if (fileName != null && !fileName.isEmpty()) {
            loadDocument(new File(fileName), false);
        } else {
            var currentFilePath = preferences.get(PREF_CURRENT_FILE, null);
            if (currentFilePath != null) {
                loadDocument(new File(currentFilePath), true);
            }
        }

        Platform.runLater(cardTreeView::requestFocus);

        // Main menu items
        newCardMenuItem.disableProperty().bind(currentFile.isNull()
                .or(searchTextField.textProperty().isEmpty().not()));
        newCategoryMenuItem.disableProperty().bind(currentFile.isNull()
                .or(searchTextField.textProperty().isEmpty().not()));
        newNoteMenuItem.disableProperty().bind(currentFile.isNull()
                .or(searchTextField.textProperty().isEmpty().not()));
        changePasswordMenuItem.disableProperty().bind(currentFile.isNull());
        deleteMenuItem.disableProperty().bind(currentFile.isNull());

        // Context menu items
        ctxNewCardMenuItem.disableProperty().bind(newCardMenuItem.disableProperty());
        ctxNewNoteMenuItem.disableProperty().bind(newNoteMenuItem.disableProperty());
        ctxNewCategoryMenuItem.disableProperty().bind(newCategoryMenuItem.disableProperty());
        ctxCutMenuItem.disableProperty().bind(searchTextField.textProperty().isEmpty().not());
        ctxDeleteMenuItem.disableProperty().bind(currentFile.isNull());
        ctxDeleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
    }

    ReadOnlyStringProperty searchTextProperty() {
        return searchTextField.textProperty();
    }

    private void doSearch(String newValue) {
        if (newValue.isEmpty()) {
            cardTreeView.setRoot(rootTreeItem);
        } else {
            var root = new TreeItem<Record>();
            searchTree(rootTreeItem, newValue.toLowerCase())
                    .forEach(x -> root.getChildren().add(new TreeItem<>(x)));
            cardTreeView.setRoot(root);
        }
    }

    private List<Record> getAll(TreeItem<Record> root) {
        var result = new ArrayList<Record>();

        for (var child : root.getChildren()) {
            var r = child.getValue();
            if (r instanceof Link) {
                continue;
            }

            if (r instanceof Category) {
                result.addAll(getAll(child));
            } else {
                result.add(r);
            }
        }

        return result;
    }

    private List<Record> searchTree(TreeItem<Record> root, String newValue) {
        var result = new ArrayList<Record>();

        for (var child : root.getChildren()) {
            var r = child.getValue();
            if (r instanceof Category) {
                if (r.getName().toLowerCase().contains(newValue)) {
                    // Category name matched -> add entire subtree
                    result.addAll(getAll(child));
                } else {
                    result.addAll(searchTree(child, newValue));
                }
            } else {
                if (!(r instanceof Link) && r.getName().toLowerCase().contains(newValue)) {
                    result.add(r);
                }
            }
        }

        return result;
    }

    static MainWindowController getMainWindow() {
        return mainWindowController;
    }

    private Optional<TreeItem<Record>> getSelectedItem() {
        return Optional.ofNullable(cardTreeView.getSelectionModel().getSelectedItem());
    }

    private void loadDocument(File file, boolean changeSettings) {
        if (!file.exists()) {
            currentFile.set(null);
            if (changeSettings) {
                preferences.put(PREF_CURRENT_FILE, "");
            }
        } else {
            new PasswordDialog(file, false).showAndWait().ifPresent(password -> {
                currentPassword = password;

                try (var in = new FileInputStream(file)) {
                    if (!currentPassword.isEmpty()) {
                        try (InputStream cin = AES.aes256().getInputStream(in, password)) {
                            rootTreeItem = Serializer.deserialize(cin);
                        }
                    } else {
                        rootTreeItem = Serializer.deserialize(in);
                    }

                    cardTreeView.setRoot(rootTreeItem);

                    currentFile.set(file);
                    if (changeSettings) {
                        preferences.put(PREF_CURRENT_FILE, currentFile.get().getAbsolutePath());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    void writeDocument() {
        Objects.requireNonNull(currentFile);
        writeDocument(currentFile.get());
    }

    private void writeDocument(File file) {
        try (var bOut = new ByteArrayOutputStream()) {
            if (!currentPassword.isEmpty()) {
                try (var cout = AES.aes256().getOutputStream(bOut, currentPassword)) {
                    Serializer.serialize(cout, cardTreeView.getRoot());
                }
            } else {
                Serializer.serialize(bOut, cardTreeView.getRoot());
            }

            try (var fOut = new FileOutputStream(file)) {
                fOut.write(bOut.toByteArray());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void onExit() {
        if (currentFile.get() != null) {
            writeDocument();
        }
        System.exit(0);
    }

    private void onNewFile() {
        var d = new FileChooser();
        d.setTitle("New File");
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showSaveDialog(null);
        if (file != null) {
            new PasswordDialog(file, true).showAndWait().ifPresent(password -> {
                currentPassword = password;

                rootRecord = new Category("root", RecordType.EMPTY, Picture.FOLDER);

                var root = new TreeItem<Record>(rootRecord);
                cardTreeView.setRoot(root);
                root.setExpanded(true);

                writeDocument(file);
                currentFile.set(file);
                preferences.put(PREF_CURRENT_FILE, currentFile.get().getAbsolutePath());
            });
        }
    }

    private void onOpenFile() {
        var d = new FileChooser();
        d.setTitle("Open File");
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        );
        var file = d.showOpenDialog(null);
        if (file != null) {
            loadDocument(file, true);
        }
    }

    private void processNewRecord(NewRecordDescriptor<? extends Record> recordDescriptor) {
        TreeItem<Record> parentItem;

        if (recordDescriptor.isParentRoot()) {
            parentItem = cardTreeView.getRoot();
        } else {
            parentItem = cardTreeView.getSelectionModel().getSelectedItem();
            if (parentItem == null || !(parentItem.getValue() instanceof Category)) {
                parentItem = cardTreeView.getRoot();
            }
        }

        parentItem.getChildren().add(new TreeItem<>(recordDescriptor.getRecord()));
        parentItem.setExpanded(true);

        writeDocument();
    }

    private void onDeleteRecord() {
        getSelectedItem().ifPresent((TreeItem<Record> item) -> {
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().filter(x -> x == ButtonType.YES).ifPresent(x -> {
                var id = item.getValue().getId();

                var parent = item.getParent();
                parent.getChildren().remove(item);

                deleteBrokenLinks(id, cardTreeView.getRoot());

                writeDocument();
            });
        });
    }

    private void onNewCard() {
        var selected = cardTreeView.getSelectionModel().getSelectedItem();
        var defaultType = selected != null && selected.getValue() instanceof Category ?
                selected.getValue().getType() : RecordType.PASSWORD;

        new CardDialog(defaultType, null)
                .showAndWait()
                .ifPresent(this::processNewRecord);
    }

    private void onNewCategory() {
        new CategoryDialog(null)
                .showAndWait()
                .ifPresent(this::processNewRecord);
    }

    private void onNewNote() {
        new NoteDialog().showAndWait().ifPresent(this::processNewRecord);
    }

    private void setupRecordViewer(TreeItem<Record> item) {
        var record = item.getValue();

        if (record instanceof Link) {
            var target = findRecordById(((Link) record).getTargetId(), cardTreeView.getRoot());
            target.ifPresent(this::setupRecordViewer);
        } else {
            cardContentTitleLabel.setText(item.getValue().getName());
            cardContentTitleLabel.setGraphic(new ImageView(item.getValue().getPicture().getBigImage()));

            if (record instanceof Note) {
                noteViewer.setText(((Note) record).getText());
                recordViewPane.setCenter(noteViewer);
            } else {
                if (record instanceof Card) {
                    recordViewPane.setCenter(cardContentView);

                    var wrappers = ((Card) record).getFields().stream()
                            .filter(f -> !f.getValue().isEmpty())
                            .map(FieldWrapper::new).collect(Collectors.toList());
                    cardContentView.setData(FXCollections.observableArrayList(wrappers), ((Card) record).getNote());
                }
            }
        }
    }

    private void onTreeViewSelected() {
        var item = cardTreeView.getSelectionModel().getSelectedItem();

        if (item == null || item.getValue() instanceof Category) {
            recordViewPane.setCenter(null);
            cardContentTitleLabel.setText(null);
            cardContentTitleLabel.setGraphic(null);
        } else {
            setupRecordViewer(item);
        }
    }

    private void processEditedRecord(TreeItem<Record> item, Record r) {
        item.setValue(r);
        onTreeViewSelected();
        writeDocument();
    }

    public void onEditCard() {
        var item = cardTreeView.getSelectionModel().getSelectedItem();

        if (item != null) {
            if (item.getValue() instanceof Card) {
                new EditCardDialog((Card) item.getValue())
                        .showAndWait().ifPresent(result -> processEditedRecord(item, result));
            } else {
                if (item.getValue() instanceof Note) {
                    new EditNoteDialog((Note) item.getValue())
                            .showAndWait().ifPresent(result -> processEditedRecord(item, result));
                } else {
                    if (item.getValue() instanceof Category) {
                        new EditCategoryDialog((Category) item.getValue())
                                .showAndWait().ifPresent(result -> processEditedRecord(item, result));
                    }
                }

            }
        }
    }

    private void onChangePassword() {
        new PasswordDialog(currentFile.get(), true).showAndWait().ifPresent(password -> {
            currentPassword = password;
            writeDocument(currentFile.get());
        });
    }

    private void onAbout() {
        new AboutDialog().showAndWait();
    }

    public void onImport() {
        var d = new FileChooser();
        d.setTitle("Open eWallet Text File");
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("eWallet Text Files", "*.txt")
        );
        var file = d.showOpenDialog(null);
        if (file != null) {
            var root = ImportExport.eWalletImport(file);
            if (root != null) {
                cardTreeView.setRoot(root);
                writeDocument();
            }
        }
    }

    public void onExport() {
        var d = new FileChooser();
        d.setTitle("Password Manager XML");
        d.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Password Manager XML Files", "*.xml")
        );
        var file = d.showSaveDialog(null);
        if (file != null) {
            try (var out = new FileOutputStream(file)) {
                Serializer.serialize(out, cardTreeView.getRoot());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void onCardTreeContextMenuShowing() {
        var pasteEnable = false;

        var cb = Clipboard.getSystemClipboard();
        var targetItem = getSelectedItem();

        if (cb.hasContent(Record.DATA_FORMAT) && targetItem.isPresent()) {
            var sourceId = (String) cb.getContent(Record.DATA_FORMAT);
            var sourceItem = findRecordById(sourceId, cardTreeView.getRoot());

            if (sourceItem.isPresent()) {
                pasteEnable = checkForParentCategory(sourceItem.get(), targetItem.get());
            }
        }


        ctxCardPasteMenuItem.setDisable(!pasteEnable || !searchTextField.getText().isEmpty());
        ctxCardPasteLinkMenuItem.setDisable(!pasteEnable || cut || searchTextField.getText().isEmpty());
    }

    private boolean checkForParentCategory(TreeItem item, TreeItem target) {
        // Leaf node, can be moved anywhere
        if (item.getChildren().isEmpty()) {
            return true;
        }

        var parent = target.getParent();
        while (parent != null) {
            if (parent == item) {
                return false;
            }
            parent = parent.getParent();
        }

        return true;
    }

    private void putCardToClipboard(Record record) {
        var cb = Clipboard.getSystemClipboard();
        var content = new ClipboardContent();
        content.put(Record.DATA_FORMAT, record.getId());
        cb.setContent(content);
    }

    private void onCardCut() {
        getSelectedItem().ifPresent(item -> {
            putCardToClipboard(item.getValue());
            cut = true;
        });
    }

    private void onCardCopy() {
        getSelectedItem().ifPresent(item -> {
            putCardToClipboard(item.getValue());
            cut = false;
        });
    }

    private void genericPaste(Function<TreeItem<Record>, TreeItem<Record>> getNewItem) {
        getSelectedItem().ifPresent(targetItem -> {
            var cb = Clipboard.getSystemClipboard();
            var sourceId = (String) cb.getContent(Record.DATA_FORMAT);

            findRecordById(sourceId, cardTreeView.getRoot()).ifPresent(sourceItem -> {
                var newItem = getNewItem.apply(sourceItem);

                if (targetItem.getValue() instanceof Category) {
                    if (sourceItem != targetItem) {
                        if (cut) {
                            sourceItem.getParent().getChildren().remove(sourceItem);
                        }
                        targetItem.getChildren().add(newItem);
                        targetItem.setExpanded(true);
                    }
                } else {
                    if (cut) {
                        sourceItem.getParent().getChildren().remove(sourceItem);
                    }
                    var parentItem = targetItem.getParent();
                    int index = parentItem.getChildren().indexOf(targetItem);
                    parentItem.getChildren().add(index + 1, newItem);
                }

            });
        });
    }

    private void onCardPaste() {
        genericPaste(sourceItem -> cut ? sourceItem : new TreeItem<>(sourceItem.getValue().cloneWithNewId()));
    }

    private void onCardPasteLink() {
        genericPaste(sourceItem -> new TreeItem<>(new Link(sourceItem.getValue().getId())));
    }

    static Optional<TreeItem<Record>> findRecordById(String id, TreeItem<Record> item) {
        if (id == null) {
            return Optional.empty();
        }

        if (item.getValue().getId().equals(id)) {
            return Optional.of(item);
        } else {
            for (TreeItem<Record> child : item.getChildren()) {
                var found = findRecordById(id, child);
                if (found.isPresent()) {
                    return found;
                }
            }
            return Optional.empty();
        }
    }

    private static void deleteBrokenLinks(String id, TreeItem<Record> root) {
        root.getChildren().stream()
                .filter(x -> !x.getChildren().isEmpty()).forEach(x -> deleteBrokenLinks(id, x));

        var toDelete = root.getChildren().stream()
                .filter(x -> x.getValue() instanceof Link)
                .filter(x -> ((Link) x.getValue()).getTargetId().equals(id))
                .collect(Collectors.toList());

        toDelete.forEach(x -> root.getChildren().remove(x));
    }

    /*
        Autoscroll implementation, based on:
        http://programmingtipsandtraps.blogspot.ru/2015/10/drag-and-drop-in-treetableview-with.html
     */

    private double scrollDirection = 0;
    private final Timeline scrollTimeline = new Timeline();

    private Optional<ScrollBar> getVerticalScrollbar() {
        return cardTreeView.lookupAll(".scroll-bar").stream()
                .filter(n -> n instanceof ScrollBar)
                .map(n -> (ScrollBar) n)
                .filter(n -> n.getOrientation().equals(Orientation.VERTICAL))
                .findFirst();
    }

    private void dragScroll() {
        getVerticalScrollbar().ifPresent(sb -> {
            var newValue = sb.getValue() + scrollDirection;
            newValue = Math.min(newValue, 1.0);
            newValue = Math.max(newValue, 0.0);
            sb.setValue(newValue);
        });
    }

    private void setupScrolling() {
        scrollTimeline.setCycleCount(Timeline.INDEFINITE);
        scrollTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), "Scroll", (ActionEvent) -> dragScroll()));
        cardTreeView.setOnDragExited(event -> {
            if (event.getY() > 0) {
                scrollDirection = 1.0 / cardTreeView.getExpandedItemCount();
            } else {
                scrollDirection = -1.0 / cardTreeView.getExpandedItemCount();
            }
            scrollTimeline.play();
        });
        cardTreeView.setOnDragEntered(event -> scrollTimeline.stop());
        cardTreeView.setOnDragDone(event -> scrollTimeline.stop());
    }
}
