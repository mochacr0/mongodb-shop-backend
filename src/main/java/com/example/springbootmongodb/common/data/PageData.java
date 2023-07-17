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

    @Schema(description = "Có trang tiếp theo hay không", accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty("hasNext")
    private boolean hasNext;

    @Schema(description = "Tổng sổ phần tử trong tất cả các trang", accessMode = Schema.AccessMode.READ_ONLY)
    private long totalElements;

    @Schema(description = "Tổng số trang", accessMode = Schema.AccessMode.READ_ONLY)
    private long totalPages;

    @Schema(description = "Danh sách phần tử", accessMode = Schema.AccessMode.READ_ONLY)
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
