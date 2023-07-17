package com.example.springbootmongodb.common.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Sort;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageParameter {
    @Schema(description = "Số trang hiện tại (mặc định là trang đầu tiên)", example = "0")
    private int page = 0;
    @Schema(description = "Số lượng phần tử tối đa trong mỗi trang", example = "10")
    private int pageSize = 10;
    @Schema(description = "Thuộc tính sắp xếp phần tử được áp dụng", example = "createdAt")
    private String sortProperty = DEFAULT_SORT_PROPERTY;
    @Schema(description = "Chiều sắp xếp (tăng dần hoặc giảm dần)", example = "desc")
    private String sortDirection = DEFAULT_SORT_DIRECTION.name();
    @Schema(description = "Từ khóa muốn tìm kiếm")
    private String textSearch = "";

    public static final String DEFAULT_SORT_PROPERTY = "createdAt";
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;
    public static final Sort DEFAULT_SORT = Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_PROPERTY);
    public static final String DEFAULT_TEXT_SEARCH = "";

    public Sort toSort() {
        return Sort.by(Sort.Direction.fromString(sortDirection), sortProperty);
    }
}