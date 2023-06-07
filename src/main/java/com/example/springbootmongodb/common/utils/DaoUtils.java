package com.example.springbootmongodb.common.utils;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.ToData;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class DaoUtils {
    public static <D> PageData<D> toPageData(Page<? extends ToData<D>> page) {
        List<D> data = page.getContent().stream().map(ToData::toData).collect(Collectors.toList());
        return new PageData<>(page.hasNext(), page.getTotalElements(), page.getTotalPages(), data);
    }

    public static <D> List<D> toListData(List<? extends ToData<D>> list) {
        return list.stream().map(ToData::toData).collect(Collectors.toList());
    }
    public static <D> D toData(ToData<D> entity) {
        D data = null;
        if (entity != null) {
            data = entity.toData();
        }
        return data;
    }
    public static <D> D toData(Optional<? extends ToData<D>> optionalEntity) {
        return toData(optionalEntity.orElse(null));
    }

    public static Pageable toPageable(PageParameter pageParameter) {
        return PageRequest.of(pageParameter.getPage(), pageParameter.getPageSize(), pageParameter.toSort());
    }
}
