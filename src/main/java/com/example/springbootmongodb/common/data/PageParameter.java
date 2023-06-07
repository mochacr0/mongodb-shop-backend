package com.example.springbootmongodb.common.data;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class PageParameter {
    private int page;
    private int pageSize;
    private String sortProperty;
    private String sortDirection;
    private String textSearch;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT_PROPERTY = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = Sort.Direction.DESC.name();
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, DEFAULT_SORT_PROPERTY);
    private static final String DEFAULT_TEXT_SEARCH = "";

    public PageParameter(int page, int pageSize, String sortDirection, String sortProperty, String textSearch) {
        this.page = page;
        this.pageSize = pageSize;
        this.sortDirection = sortDirection;
        this.sortProperty = sortProperty;
        this.textSearch = textSearch;
    }

    public PageParameter() {
        this.page = DEFAULT_PAGE;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.sortDirection = Sort.Direction.DESC.name();
        this.sortProperty = DEFAULT_SORT_PROPERTY;
        this.textSearch = "";
    }

    public PageParameter(int page) {
        this(page, DEFAULT_PAGE_SIZE);
    }

    public PageParameter(int page, int pageSize) {
        this(page, pageSize, DEFAULT_SORT_DIRECTION);
    }

    public PageParameter(int page, int pageSize, String sortDirection) {
        this(page, pageSize, sortDirection, DEFAULT_SORT_PROPERTY);
    }

    public PageParameter(int page, int pageSize, String sortDirection, String sortProperty) {
        this(page, pageSize, sortDirection, sortProperty, DEFAULT_TEXT_SEARCH);
    }

    public PageParameter nextPageParameter() {
        return new PageParameter(this.page + 1, this.pageSize, this.sortDirection, this.sortProperty, this.textSearch);
    }
    public Sort toSort() {
        return Sort.by(Sort.Direction.fromString(sortDirection), sortProperty);
    }
}