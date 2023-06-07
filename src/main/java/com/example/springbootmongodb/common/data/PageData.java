package com.example.springbootmongodb.common.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Setter
@NoArgsConstructor
@Schema
public class PageData<T> {

    @Schema(description = "boolean indicates more pages", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("hasNext")
    private boolean hasNext;

    @Schema(description = "Total number of elements in all available pages", accessMode = Schema.AccessMode.READ_ONLY)
    private long totalElements;

    @Schema(description = "Total pages of available", accessMode = Schema.AccessMode.READ_ONLY)
    private long totalPages;

    @Schema(description = "Array of the entities", accessMode = Schema.AccessMode.READ_ONLY)
    private List<T> data;

    public <D> PageData(Page<? extends ToData<T>> page) {
        this.hasNext = page.hasNext();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.data = page.getContent().stream().map(ToData::toData).collect(Collectors.toList());
    }
    public <D> PageData(boolean hasNext, long totalElements, long totalPages, List<T> data) {
        this.hasNext = hasNext;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.data = data;
    }

    public boolean hasNext() {
        return hasNext;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public List<T> getData() {
        return data;
    }
}
