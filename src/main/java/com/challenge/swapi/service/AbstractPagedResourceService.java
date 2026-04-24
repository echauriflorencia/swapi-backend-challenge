package com.challenge.swapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPagedResourceService<T, R> {

    private static final int DEFAULT_FETCH_PAGE_SIZE = 50;

    protected R getResources(String id, String nameOrTitle, int page, int size) {
        if (!hasFilters(id, nameOrTitle)) {
            return fetchPage(page, size);
        }

        List<T> filtered = fetchAllResources().stream()
                .filter(resource -> matchesId(resource, id))
                .filter(resource -> matchesNameOrTitle(resource, nameOrTitle))
                .collect(Collectors.toList());

        return buildFilteredResponse(paginate(filtered, page, size), filtered.size(), size);
    }

    protected int calculateTotalPages(int totalRecords, int pageSize) {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    protected boolean containsIgnoreCase(String value, String filter) {
        return filter == null || filter.isBlank()
                || value != null && value.toLowerCase().contains(filter.toLowerCase());
    }

    protected int getFetchPageSize() {
        return DEFAULT_FETCH_PAGE_SIZE;
    }

    protected abstract R fetchPage(int page, int size);

    protected abstract List<T> extractResults(R response);

    protected abstract int extractTotalPages(R response);

    protected abstract boolean matchesId(T resource, String id);

    protected abstract boolean matchesNameOrTitle(T resource, String nameOrTitle);

    protected abstract R buildFilteredResponse(List<T> pagedResults, int totalRecords, int pageSize);

    private List<T> fetchAllResources() {
        List<T> all = new ArrayList<>();
        int page = 1;
        int totalPages = 1;

        while (page <= totalPages) {
            R response = fetchPage(page, getFetchPageSize());
            List<T> pageResults = extractResults(response);
            if (pageResults == null || pageResults.isEmpty()) {
                break;
            }

            all.addAll(pageResults);

            int reportedTotalPages = extractTotalPages(response);
            totalPages = reportedTotalPages > 0 ? Math.max(reportedTotalPages, page) : page;
            page++;
        }

        return all;
    }

    private boolean hasFilters(String id, String nameOrTitle) {
        return id != null && !id.isBlank() || nameOrTitle != null && !nameOrTitle.isBlank();
    }

    private List<T> paginate(List<T> results, int page, int size) {
        int fromIndex = (page - 1) * size;
        if (fromIndex >= results.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(fromIndex + size, results.size());
        return results.subList(fromIndex, toIndex);
    }
}