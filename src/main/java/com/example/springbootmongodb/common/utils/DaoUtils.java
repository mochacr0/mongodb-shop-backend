package com.example.springbootmongodb.common.utils;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.ToData;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.model.AbstractEntity;
import com.example.springbootmongodb.model.UserAddressEntity;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor
public class DaoUtils {
    public static <D> PageData<D> toPageData(Page<? extends ToData<D>> page) {
        List<D> data = page.getContent().stream().map(ToData::toData).collect(Collectors.toList());
        return new PageData<>(page.hasNext(), page.getTotalElements(), page.getTotalPages(), data);
    }

    public static <D, E extends AbstractEntity> PageData<D> toPageData(Page<E> page, Function<E, D> fromEntity) {
        List<D> data = page.getContent().stream().map(fromEntity::apply).collect(Collectors.toList());
        return new PageData<>(page.hasNext(), page.getTotalElements(), page.getTotalPages(), data);
    }

    public static <D> List<D> toListData(List<? extends ToData<D>> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(ToData::toData).collect(Collectors.toList());
    }

    public static <D, E extends AbstractEntity> List<D> toListData(List<E> list, Function<E, D> fromEntity) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(fromEntity::apply).collect(Collectors.toList());
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

    public static <D, E extends AbstractEntity> D toData(Optional<E> optionalEntity, Function<E, D> fromEntity) {
        return optionalEntity.map(fromEntity::apply).orElse(null);
    }

    public static <D, E extends AbstractEntity> D toData(E entity, Function<E, D> toData) {
        return entity == null ? null : toData.apply(entity);
    }

    public static Pageable toPageable(PageParameter pageParameter) {
        return PageRequest.of(pageParameter.getPage(), pageParameter.getPageSize(), pageParameter.toSort());
    }

}
