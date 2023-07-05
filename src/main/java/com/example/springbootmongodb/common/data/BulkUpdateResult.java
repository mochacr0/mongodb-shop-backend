package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
@Getter
@Setter
public class BulkUpdateResult<T extends AbstractEntity> {
    private List<T> data;
    private AtomicBoolean isModified;
}

