// Copyright © 2026 Petr Panteleyev
// SPDX-License-Identifier: BSD-2-Clause
package org.panteleyev.pwdmanager.service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.panteleyev.fx.PredicateProperty;
import org.panteleyev.pwdmanager.filters.FieldContentFilter;
import org.panteleyev.pwdmanager.filters.RecordNameFilter;
import org.panteleyev.pwdmanager.model.WalletRecord;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.panteleyev.pwdmanager.model.Card.COMPARE_BY_ACTIVE;
import static org.panteleyev.pwdmanager.model.Card.COMPARE_BY_FAVORITE;
import static org.panteleyev.pwdmanager.model.Card.COMPARE_BY_NAME;

/**
 * Service responsible for managing record sorting, filtering, and search.
 *
 * <p>Encapsulates the {@link SortedList} and {@link FilteredList} chain,
 * along with search predicate management and the "show deleted" toggle.</p>
 *
 * <p>The service exposes the filtered list for binding to a {@code ListView}
 * and provides methods to update search and filter state.</p>
 */
public final class SearchService {

    private final ObservableList<WalletRecord> recordList;
    private final FilteredList<WalletRecord> filteredList;
    private final PredicateProperty<WalletRecord> defaultFilter;
    private final BooleanProperty showDeletedRecords;

    /**
     * Creates a new SearchService with the given record list.
     *
     * @param recordList the observable list of wallet records to manage
     */
    public SearchService(ObservableList<WalletRecord> recordList) {
        this.recordList = requireNonNull(recordList, "recordList must not be null");
        this.showDeletedRecords = new SimpleBooleanProperty(false);
        this.defaultFilter = new PredicateProperty<>(WalletRecord::active);

        var sortedList = createSortedList(recordList);
        filteredList = createFilteredList(sortedList, defaultFilter);

        showDeletedRecords.addListener((_, _, newValue) ->
                defaultFilter.set(newValue ? _ -> true : WalletRecord::active));
    }

    /**
     * Returns the sorted and filtered list for binding to a ListView.
     *
     * @return the filtered list
     */
    public FilteredList<WalletRecord> getFilteredList() {
        return filteredList;
    }

    /**
     * Returns the underlying observable record list.
     *
     * @return the record list
     */
    public ObservableList<WalletRecord> getRecordList() {
        return recordList;
    }

    /**
     * Returns the "show deleted records" property.
     *
     * @return the show deleted property
     */
    public BooleanProperty showDeletedRecordsProperty() {
        return showDeletedRecords;
    }

    /**
     * Performs a search with the given text.
     *
     * <p>If the text is empty, the default filter (active-only or show-all) is applied.
     * Otherwise, a combined predicate is used that checks both the default filter and the search criteria (record name
     * and field content).</p>
     *
     * @param searchText the search text to filter by
     */
    public void doSearch(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredList.predicateProperty().bind(defaultFilter);
        } else {
            var searchPredicate = new PredicateProperty<>(
                    new RecordNameFilter(searchText).or(new FieldContentFilter(searchText))
            );
            filteredList.predicateProperty().bind(
                    PredicateProperty.and(List.of(defaultFilter, searchPredicate))
            );
        }
    }

    private static SortedList<WalletRecord> createSortedList(ObservableList<WalletRecord> list) {
        var sortedList = new SortedList<>(list);
        sortedList.setComparator(COMPARE_BY_ACTIVE
                .thenComparing(COMPARE_BY_FAVORITE)
                .thenComparing(COMPARE_BY_NAME));
        return sortedList;
    }

    private static FilteredList<WalletRecord> createFilteredList(
            ObservableList<WalletRecord> list, PredicateProperty<WalletRecord> predicate)
    {

        var filteredList = list.filtered(predicate);
        filteredList.predicateProperty().bind(predicate);
        return filteredList;
    }
}
