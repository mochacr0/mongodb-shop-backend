package com.example.springbootmongodb.common.data;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageParameter {
    private int page = 0;
    private int pageSize = 10;
    private String sortProperty = DEFAULT_SORT_PROPERTY;
    private String sortDirection = DEFAULT_SORT_DIRECTION.name();
    private String textSearch = "";

    public static final String DEFAULT_SORT_PROPERTY = "createdAt";
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;
    public static final Sort DEFAULT_SORT = Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_PROPERTY);
    public static final String DEFAULT_TEXT_SEARCH = "";

    public Sort toSort() {
        return Sort.by(Sort.Direction.fromString(sortDirection), sortProperty);
    }
}