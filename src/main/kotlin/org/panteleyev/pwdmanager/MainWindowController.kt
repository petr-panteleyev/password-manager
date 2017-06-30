/*
 * Copyright (c) 2017, Petr Panteleyev <petr@panteleyev.org>
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

package org.panteleyev.pwdmanager

import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.ContextMenu
import javafx.scene.control.Label
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.ScrollBar
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.control.SplitPane
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.util.Duration
import org.controlsfx.control.textfield.TextFields
import org.panteleyev.crypto.AES
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.prefs.Preferences

internal class MainWindowController : BorderPane() {
    private val rb = PasswordManagerApplication.bundle

    // TreeView clipboard
    private var cut = false

    private val cardTreeView = TreeView<Record>()
    private val leftPane = BorderPane(cardTreeView)
    private val treeViewPane = TitledPane("", leftPane)

    private val ctxNewCardMenuItem = MenuItem(rb.getString("menu.edit.newCard"))
    private val ctxNewNoteMenuItem = MenuItem(rb.getString("menu.edit.newNote"))
    private val ctxNewCategoryMenuItem = MenuItem(rb.getString("menu.edit.newCategory"))
    private val ctxCutMenuItem = MenuItem(rb.getString("menu.edit.cut"))
    private val ctxDeleteMenuItem = MenuItem(rb.getString("menu.edit.delete"))
    private val ctxCardPasteMenuItem = MenuItem(rb.getString("menu.edit.paste"))
    private val ctxCardPasteLinkMenuItem = MenuItem(rb.getString("menu.edit.pasteLink"))

    private var searchTextField: TextField? = null
    private val cardContentTitleLabel = Label()
    private val cardEditButton = Button("Edit...")
    private val recordViewPane = BorderPane()

    // Menu items
    private val newCardMenuItem = MenuItem(rb.getString("menu.edit.newCard"))
    private val newCategoryMenuItem = MenuItem(rb.getString("menu.edit.newCategory"))
    private val newNoteMenuItem = MenuItem(rb.getString("menu.edit.newNote"))
    private val changePasswordMenuItem = MenuItem(rb.getString("menu.tools.changePassword"))
    private val deleteMenuItem = MenuItem(rb.getString("menu.edit.delete"))

    private val noteViewer = NoteViewer()
    private val cardContentView = CardViewer()

    private var rootRecord: Record? = null
    private var rootTreeItem: TreeItem<Record>? = null       // store root item for full tree

    private val currentFile = SimpleObjectProperty<File>()
    private var currentPassword: String? = null

    private val preferences = Preferences.userNodeForPackage(MainWindowController::class.java)

    private var scrollDirection = 0.0
    private val scrollTimeline = Timeline()

    init {
        createMainMenu()
        createControls()
        createCardTreeContextMenu()
        initialize()
        setupScrolling()
    }

    private fun createMainMenu() {
        // File
        val fileNewMenuItem = MenuItem(rb.getString("menu.file.new"))
        fileNewMenuItem.accelerator = KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN)
        fileNewMenuItem.setOnAction { onNewFile() }

        val fileOpenMenuitem = MenuItem(rb.getString("menu.file.open"))
        fileOpenMenuitem.accelerator = KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN)
        fileOpenMenuitem.setOnAction { onOpenFile() }

        val fileExitMenuItem = MenuItem(rb.getString("menu.file.exit"))
        fileExitMenuItem.setOnAction { onExit() }

        val fileMenu = Menu(rb.getString("menu.file"), null,
                fileNewMenuItem, fileOpenMenuitem, SeparatorMenuItem(), fileExitMenuItem)

        // Edit
        newCategoryMenuItem.setOnAction { onNewCategory() }
        newCategoryMenuItem.accelerator = KeyCodeCombination(KeyCode.T, KeyCombination.SHORTCUT_DOWN)

        newCardMenuItem.setOnAction { onNewCard() }
        newNoteMenuItem.setOnAction { onNewNote() }

        val editMenu = Menu(rb.getString("menu.edit"), null,
                newCategoryMenuItem, SeparatorMenuItem(),
                newCardMenuItem, newNoteMenuItem, SeparatorMenuItem(),
                deleteMenuItem)

        // Tools
        changePasswordMenuItem.setOnAction { onChangePassword() }
        val toolsMenu = Menu(rb.getString("menu.tools"), null, changePasswordMenuItem)

        // Help
        val helpAboutMenuItem = MenuItem(rb.getString("menu.help.about"))
        helpAboutMenuItem.setOnAction { onAbout() }
        val helpMenu = Menu(rb.getString("menu.help"), null, helpAboutMenuItem)

        val menuBar = MenuBar(fileMenu, editMenu, toolsMenu, helpMenu)
        menuBar.isUseSystemMenuBar = true

        top = menuBar
    }

    private fun createControls() {
        cardTreeView.isShowRoot = false
        BorderPane.setAlignment(cardTreeView, Pos.CENTER)

        cardEditButton.setOnAction { onEditCard() }

        val buttonBar = ButtonBar()
        buttonBar.buttons.setAll(cardEditButton)

        recordViewPane.top = cardContentTitleLabel
        recordViewPane.bottom = buttonBar

        BorderPane.setMargin(buttonBar, Insets(5.0, 5.0, 5.0, 5.0))

        cardContentTitleLabel.styleClass.add(Styles.CARD_CONTENT_TITLE)
        BorderPane.setAlignment(cardContentTitleLabel, Pos.CENTER)
        BorderPane.setAlignment(buttonBar, Pos.CENTER)

        treeViewPane.maxHeight = java.lang.Double.MAX_VALUE
        treeViewPane.maxWidth = java.lang.Double.MAX_VALUE

        val split = SplitPane(treeViewPane, recordViewPane)
        split.setDividerPositions(0.30)

        center = split
    }

    private fun createCardTreeContextMenu() {
        ctxNewCategoryMenuItem.setOnAction { onNewCategory() }
        ctxNewCardMenuItem.setOnAction { onNewCard() }
        ctxNewNoteMenuItem.setOnAction { onNewNote() }
        ctxDeleteMenuItem.setOnAction { onDeleteRecord() }
        ctxCutMenuItem.setOnAction { onCardCut() }
        ctxCardPasteMenuItem.setOnAction { onCardPaste() }
        ctxCardPasteLinkMenuItem.setOnAction { onCardPasteLink() }

        val copyMenuItem = MenuItem(rb.getString("menu.edit.copy"))
        copyMenuItem.setOnAction { onCardCopy() }

        val menu = ContextMenu(
                ctxNewCategoryMenuItem,
                SeparatorMenuItem(),
                ctxNewCardMenuItem,
                ctxNewNoteMenuItem,
                SeparatorMenuItem(),
                ctxDeleteMenuItem,
                SeparatorMenuItem(),
                ctxCutMenuItem,
                copyMenuItem,
                ctxCardPasteMenuItem,
                ctxCardPasteLinkMenuItem
        )

        menu.setOnShowing { onCardTreeContextMenuShowing() }

        cardTreeView.contextMenu = menu
    }

    private fun initialize() {
        mainWindow = this

        cardTreeView.setCellFactory { _: TreeView<Record> -> CardTreeViewCell(this) }
        cardTreeView.isShowRoot = false
        cardTreeView.selectionModel.selectedItemProperty().addListener { _ -> onTreeViewSelected() }

        cardEditButton.disableProperty().bind(cardTreeView.selectionModel.selectedItemProperty().isNull)

        currentFile.addListener { _: ObservableValue<out File>, _: File?, newValue: File? ->
            treeViewPane.text = newValue?.name?: "" }

        searchTextField = TextFields.createClearableTextField()
        searchTextField!!.textProperty().addListener { _, oldValue, newValue ->
            if (oldValue != newValue) {
                doSearch(newValue)
            }
        }
        leftPane.top = searchTextField
        BorderPane.setMargin(searchTextField, Insets(0.0, 0.0, 10.0, 0.0))

        // Cmd parameter overrides stored file but does not overwrite the setting.
        val params = PasswordManagerApplication.application!!.parameters
        val fileName = params.named["file"]
        if (fileName != null && !fileName.isEmpty()) {
            loadDocument(File(fileName), false)
        } else {
            val currentFilePath = preferences.get(PREF_CURRENT_FILE, null)
            if (currentFilePath != null) {
                loadDocument(File(currentFilePath), true)
            }
        }

        Platform.runLater { cardTreeView.requestFocus() }

        // Main menu items
        newCardMenuItem.disableProperty().bind(currentFile.isNull
                .or(searchTextField!!.textProperty().isEmpty.not()))
        newCategoryMenuItem.disableProperty().bind(currentFile.isNull
                .or(searchTextField!!.textProperty().isEmpty.not()))
        newNoteMenuItem.disableProperty().bind(currentFile.isNull
                .or(searchTextField!!.textProperty().isEmpty.not()))
        changePasswordMenuItem.disableProperty().bind(currentFile.isNull)
        deleteMenuItem.disableProperty().bind(currentFile.isNull)

        // Context menu items
        ctxNewCardMenuItem.disableProperty().bind(newCardMenuItem.disableProperty())
        ctxNewNoteMenuItem.disableProperty().bind(newNoteMenuItem.disableProperty())
        ctxNewCategoryMenuItem.disableProperty().bind(newCategoryMenuItem.disableProperty())
        ctxCutMenuItem.disableProperty().bind(searchTextField!!.textProperty().isEmpty.not())
        ctxDeleteMenuItem.disableProperty().bind(currentFile.isNull)
        ctxDeleteMenuItem.accelerator = KeyCodeCombination(KeyCode.DELETE)
    }

    fun searchTextProperty(): ReadOnlyStringProperty {
        return searchTextField!!.textProperty()
    }

    private fun doSearch(newValue: String) {
        if (newValue.isEmpty()) {
            cardTreeView.setRoot(rootTreeItem)
        } else {
            val root = TreeItem<Record>()
            searchTree(rootTreeItem!!, newValue.toLowerCase())
                    .forEach { x -> root.children.add(TreeItem(x)) }
            cardTreeView.setRoot(root)
        }
    }

    private fun getAll(root: TreeItem<Record>): List<Record> {
        val result = ArrayList<Record>()

        for (child in root.children) {
            val r = child.value
            if (r is Link) {
                continue
            }

            if (r is Category) {
                result.addAll(getAll(child))
            } else {
                result.add(r)
            }
        }

        return result
    }

    private fun searchTree(root: TreeItem<Record>, newValue: String): List<Record> {
        val result = ArrayList<Record>()

        for (child in root.children) {
            val r = child.value
            if (r is Category) {
                if (r.name.toLowerCase().contains(newValue)) {
                    // Category name matched -> add entire subtree
                    result.addAll(getAll(child))
                } else {
                    result.addAll(searchTree(child, newValue))
                }
            } else {
                if (r !is Link && r.name.toLowerCase().contains(newValue)) {
                    result.add(r)
                }
            }
        }

        return result
    }

    private val selectedItem: TreeItem<Record>?
        get() = cardTreeView.selectionModel.selectedItem

    private fun loadDocument(file: File, changeSettings: Boolean) {
        if (!file.exists()) {
            currentFile.set(null)
            if (changeSettings) {
                preferences.put(PREF_CURRENT_FILE, "")
            }
        } else {
            PasswordDialog(file, false).showAndWait().ifPresent { password ->
                currentPassword = password

                FileInputStream(file).use { inFile ->
                    if (!currentPassword!!.isEmpty()) {
                        AES.aes256().getInputStream(inFile, password).use {
                            rootTreeItem = Serializer.deserialize(it)
                        }
                    } else {
                        rootTreeItem = Serializer.deserialize(inFile)
                    }

                    cardTreeView.root = rootTreeItem

                    currentFile.set(file)
                    if (changeSettings) {
                        preferences.put(PREF_CURRENT_FILE, currentFile.get().absolutePath)
                    }
                }
            }
        }
    }

    fun writeDocument() {
        currentFile.get()?.let {
            writeDocument(it)
        }
    }

    private fun writeDocument(file: File) {
        ByteArrayOutputStream().use { bOut ->
            if (!currentPassword!!.isEmpty()) {
                AES.aes256().getOutputStream(bOut, currentPassword).use {
                    Serializer.serialize(it, cardTreeView.root)
                }
            } else {
                Serializer.serialize(bOut, cardTreeView.root)
            }

            FileOutputStream(file).use {
                it.write(bOut.toByteArray())
            }
        }
    }

    private fun onExit() {
        currentFile.get()?.let {
            writeDocument()
        }

        System.exit(0)
    }

    private fun onNewFile() {
        val d = FileChooser()
        d.title = "New File"
        d.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        )
        val file = d.showSaveDialog(null)
        if (file != null) {
            PasswordDialog(file, true).showAndWait().ifPresent { password ->
                currentPassword = password

                rootRecord = Category(name = "root", picture = Picture.FOLDER)

                val root = TreeItem<Record>(rootRecord)
                cardTreeView.root = root
                root.isExpanded = true

                writeDocument(file)
                currentFile.set(file)
                preferences.put(PREF_CURRENT_FILE, currentFile.get().absolutePath)
            }
        }
    }

    private fun onOpenFile() {
        val d = FileChooser()
        d.title = "Open File"
        d.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Password Manager Files", "*.pwd")
        )
        val file = d.showOpenDialog(null)
        if (file != null) {
            loadDocument(file, true)
        }
    }

    private fun processNewRecord(recordDescriptor: NewRecordDescriptor<Record>) {
        var parentItem: TreeItem<Record>?

        if (recordDescriptor.isParentRoot) {
            parentItem = cardTreeView.root
        } else {
            parentItem = cardTreeView.selectionModel.selectedItem
            if (parentItem == null || parentItem.value !is Category) {
                parentItem = cardTreeView.root
            }
        }

        parentItem!!.children.add(TreeItem(recordDescriptor.record))
        parentItem.isExpanded = true

        writeDocument()
    }

    private fun onDeleteRecord() {
        selectedItem?.let { item ->
            val alert = Alert(Alert.AlertType.CONFIRMATION, "Sure?", ButtonType.YES, ButtonType.NO)
            alert.showAndWait().filter { it == ButtonType.YES }.ifPresent {
                val id = item.value.id

                val parent = item.parent
                parent.children.remove(item)

                deleteBrokenLinks(id, cardTreeView.root)

                writeDocument()
            }
        }
    }

    private fun onNewCard() {
        val selected = cardTreeView.selectionModel.selectedItem
        val defaultType = if (selected != null && selected.value is Category)
            selected.value.type
        else
            RecordType.PASSWORD

        CardDialog(defaultType, null)
                .showAndWait()
                .ifPresent({ processNewRecord(it) })
    }

    private fun onNewCategory() {
        CategoryDialog(null)
                .showAndWait()
                .ifPresent({ processNewRecord(it) })
    }

    private fun onNewNote() {
        NoteDialog().showAndWait().ifPresent({ processNewRecord(it) })
    }

    private fun setupRecordViewer(item: TreeItem<Record>) {
        val record = item.value

        if (record is Link) {
            findRecordById(record.targetId, cardTreeView.root)?.let {
                setupRecordViewer(it)
            }
        } else {
            cardContentTitleLabel.text = item.value.name
            cardContentTitleLabel.graphic = ImageView(item.value.picture.bigImage)

            if (record is Note) {
                noteViewer.text = record.text
                recordViewPane.center = noteViewer
            } else {
                if (record is Card) {
                    recordViewPane.center = cardContentView
                    cardContentView.setData(record.fields, record.note)
                }
            }
        }
    }

    private fun onTreeViewSelected() {
        val item = cardTreeView.selectionModel.selectedItem

        if (item == null || item.value is Category) {
            recordViewPane.center = null
            cardContentTitleLabel.text = null
            cardContentTitleLabel.graphic = null
        } else {
            setupRecordViewer(item)
        }
    }

    private fun processEditedRecord(item: TreeItem<Record>, r: Record) {
        item.value = r
        onTreeViewSelected()
        writeDocument()
    }

    fun onEditCard() {
        val item = cardTreeView.selectionModel.selectedItem

        if (item != null) {
            if (item.value is Card) {
                EditCardDialog(item.value as Card)
                        .showAndWait().ifPresent { result -> processEditedRecord(item, result) }
            } else {
                if (item.value is Note) {
                    EditNoteDialog(item.value as Note)
                            .showAndWait().ifPresent { result -> processEditedRecord(item, result) }
                } else {
                    if (item.value is Category) {
                        EditCategoryDialog(item.value as Category)
                                .showAndWait().ifPresent { result -> processEditedRecord(item, result) }
                    }
                }
            }
        }
    }

    private fun onChangePassword() {
        PasswordDialog(currentFile.get(), true).showAndWait().ifPresent { password ->
            currentPassword = password
            writeDocument(currentFile.get())
        }
    }

    private fun onAbout() {
        AboutDialog().showAndWait()
    }

    @Suppress("unused")
    fun onImport() {
        val d = FileChooser()
        d.title = "Open eWallet Text File"
        d.extensionFilters.addAll(
                FileChooser.ExtensionFilter("eWallet Text Files", "*.txt")
        )
        val file = d.showOpenDialog(null)
        if (file != null) {
            val root = ImportExport.eWalletImport(file)
            if (root != null) {
                cardTreeView.root = root
                writeDocument()
            }
        }
    }

    @Suppress("unused")
    fun onExport() {
        val d = FileChooser()
        d.title = "Password Manager XML"
        d.extensionFilters.addAll(
                FileChooser.ExtensionFilter("Password Manager XML Files", "*.xml")
        )
        val file = d.showSaveDialog(null)
        if (file != null) {
            try {
                FileOutputStream(file).use { out -> Serializer.serialize(out, cardTreeView.root) }
            } catch (ex: Exception) {
                throw RuntimeException(ex)
            }

        }
    }

    private fun onCardTreeContextMenuShowing() {
        var pasteEnable = false

        val cb = Clipboard.getSystemClipboard()
        val targetItem = selectedItem

        if (cb.hasContent(Record.DATA_FORMAT) && targetItem != null) {
            val sourceId = cb.getContent(Record.DATA_FORMAT) as String
            findRecordById(sourceId, cardTreeView.root)?.let {
                pasteEnable = checkForParentCategory(it, targetItem)
            }
        }

        ctxCardPasteMenuItem.isDisable = !pasteEnable || !searchTextField!!.text.isEmpty()
        ctxCardPasteLinkMenuItem.isDisable = !pasteEnable || cut || searchTextField!!.text.isEmpty()
    }

    private fun checkForParentCategory(item: TreeItem<*>, target: TreeItem<*>): Boolean {
        // Leaf node, can be moved anywhere
        if (item.children.isEmpty()) {
            return true
        }

        var parent: TreeItem<*>? = target.parent
        while (parent != null) {
            if (parent === item) {
                return false
            }
            parent = parent.parent
        }

        return true
    }

    private fun putCardToClipboard(record: Record) {
        val cb = Clipboard.getSystemClipboard()
        val content = ClipboardContent()
        content.put(Record.DATA_FORMAT, record.id)
        cb.setContent(content)
    }

    private fun onCardCut() {
        selectedItem?.let {
            putCardToClipboard(it.value)
            cut = true
        }
    }

    private fun onCardCopy() {
        selectedItem?.let {
            putCardToClipboard(it.value)
            cut = false
        }
    }

    private fun genericPaste(getNewItem: (TreeItem<Record>) -> TreeItem<Record>) {
        selectedItem?.let { targetItem ->
            val cb = Clipboard.getSystemClipboard()
            val sourceId = cb.getContent(Record.DATA_FORMAT) as String

            findRecordById(sourceId, cardTreeView.root)?.let { sourceItem ->
                val newItem = getNewItem(sourceItem)

                if (targetItem.value is Category) {
                    if (sourceItem !== targetItem) {
                        if (cut) {
                            sourceItem.parent.children.remove(sourceItem)
                        }
                        targetItem.children.add(newItem)
                        targetItem.isExpanded = true
                    }
                } else {
                    if (cut) {
                        sourceItem.parent.children.remove(sourceItem)
                    }
                    val parentItem = targetItem.parent
                    val index = parentItem.children.indexOf(targetItem)
                    parentItem.children.add(index + 1, newItem)
                }

            }
        }
    }

    private fun onCardPaste() {
        genericPaste({ sourceItem -> if (cut) sourceItem else TreeItem<Record>(sourceItem.value.cloneWithNewId()) })
    }

    private fun onCardPasteLink() {
        genericPaste({ sourceItem -> TreeItem<Record>(Link(targetId = sourceItem.value.id)) })
    }

    /*
        Autoscroll implementation, based on:
        http://programmingtipsandtraps.blogspot.ru/2015/10/drag-and-drop-in-treetableview-with.html
     */

    private val verticalScrollbar: ScrollBar?
        get() = cardTreeView.lookupAll(".scroll-bar")
                .filter { it is ScrollBar }
                .map { it as ScrollBar }
                .filter { it.orientation == Orientation.VERTICAL }
                .firstOrNull()

    private fun dragScroll() {
        verticalScrollbar?.let {
            var newValue = it.value + scrollDirection
            newValue = Math.min(newValue, 1.0)
            newValue = Math.max(newValue, 0.0)
            it.value = newValue
        }
    }

    private fun setupScrolling() {
        scrollTimeline.cycleCount = Timeline.INDEFINITE

        scrollTimeline.keyFrames.add(KeyFrame(Duration.millis(20.0), "Scroll", EventHandler { dragScroll() }))

        cardTreeView.setOnDragExited { event ->
            if (event.y > 0) {
                scrollDirection = 1.0 / cardTreeView.expandedItemCount
            } else {
                scrollDirection = -1.0 / cardTreeView.expandedItemCount
            }
            scrollTimeline.play()
        }
        cardTreeView.setOnDragEntered { scrollTimeline.stop() }
        cardTreeView.setOnDragDone { scrollTimeline.stop() }
    }

    companion object {
        val CSS_PATH = "/PasswordManager.css"

        private val PREF_CURRENT_FILE = "currentFile"

        val CSS_DROP_TARGET = "dropTarget"
        val CSS_DROP_BELOW = "dropBelow"
        val CSS_DROP_ABOVE = "dropAbove"

        val CSS_DND_STYLES = arrayOf(CSS_DROP_TARGET, CSS_DROP_BELOW, CSS_DROP_ABOVE)

        var mainWindow: MainWindowController? = null
            private set

        fun findRecordById(id: String?, item: TreeItem<Record>): TreeItem<Record>? {
            if (id == null) {
                return null
            }

            if (item.value.id == id) {
                return item
            } else {
                for (child in item.children) {
                    val found = findRecordById(id, child)
                    if (found != null) {
                        return found
                    }
                }
                return null
            }
        }

        private fun deleteBrokenLinks(id: String, root: TreeItem<Record>) {
            root.children.filter { !it.children.isEmpty() }
                    .forEach { deleteBrokenLinks(id, it) }

            root.children.filter { it.value is Link }
                    .filter { (it.value as Link).targetId == id }
                    .forEach { root.children.remove(it) }
        }
    }
}
