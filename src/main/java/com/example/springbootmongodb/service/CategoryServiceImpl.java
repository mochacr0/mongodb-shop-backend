package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.mapper.CategoryMapper;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.common.validator.CommonValidator;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.exception.UnprocessableContentException;
import com.example.springbootmongodb.model.CategoryEntity;
import com.example.springbootmongodb.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl extends DataBaseService<CategoryEntity> implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CommonValidator commonValidator;
    private final CategoryMapper mapper;
    @Override
    public MongoRepository<CategoryEntity, String> getRepository() {
        return this.categoryRepository;
    }
    public static final String REQUIRED_CATEGORY_ID_ERROR_MESSAGE = "Category id should be specified";
    public static final String REQUIRED_CATEGORY_NAME_ERROR_MESSAGE = "Category name should be specified";
    public static final String DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE = "There is already a category with that name";
    public static final String SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE = "Cannot add a sub category to another sub category";
    public static final String DEFAULT_CATEGORY_CANNOT_BE_REMOVED_ERROR_MESSAGE = "Cannot delete default category";
    public static final String DEFAULT_CATEGORY_SINGLETON_ERROR_MESSAGE = "Only 1 default category is allowed";
    public static final String NON_EXISTENT_PARENT_CATEGORY_ERROR_MESSAGE = "Cannot refer to a non-existent parent category";

    @Override
    public CategoryEntity findById(String id) {
        log.info("Performing CategoryService findById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException(REQUIRED_CATEGORY_ID_ERROR_MESSAGE);
        }
        Optional<CategoryEntity> categoryEntityOpt = categoryRepository.findById(id);
        if (categoryEntityOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Category with id [%s] is not found", id));
        }
        return categoryEntityOpt.get();
    }

    @Override
    public CategoryEntity findByName(String name) {
        log.info("Performing CategoryService findByName");
        if (StringUtils.isEmpty(name)) {
            throw new InvalidDataException(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        Optional<CategoryEntity> categoryEntityOpt = categoryRepository.findByName(name);
        if (categoryEntityOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Category with name [%s] is not found", name));
        }
        return categoryEntityOpt.get();
    }

    @Override
    public PageData<Category> findCategories(PageParameter pageParameter) {
        log.info("Performing CategoryService find");
        commonValidator.validatePageParameter(pageParameter);
        return DaoUtils.toPageData(categoryRepository.findParentCategories(DaoUtils.toPageable(pageParameter)), mapper::fromEntity);
    }

    @Override
    @Transactional
    public CategoryEntity create(Category category) {
        log.info("Performing CategoryService create");
        if (StringUtils.isEmpty(category.getName())) {
            throw new InvalidDataException(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        if (existsByName(category.getName())) {
            throw new InvalidDataException(DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        if (category.isDefault()) {
            CategoryEntity defaultCategoryEntity = categoryRepository.findDefaultCategory();
            if (defaultCategoryEntity != null) {
                throw new InvalidDataException(DEFAULT_CATEGORY_SINGLETON_ERROR_MESSAGE);
            }
        }
        if (StringUtils.isNotEmpty(category.getParentCategoryId())) {
            CategoryEntity parentCategory;
            try {
                parentCategory = this.findById(category.getParentCategoryId());
            } catch (ItemNotFoundException ex) {
                throw new UnprocessableContentException(NON_EXISTENT_PARENT_CATEGORY_ERROR_MESSAGE);
            }
            if (StringUtils.isNotEmpty(parentCategory.getParentCategoryId())) {
                throw new InvalidDataException(SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE);
            }
        }
        return super.insert(mapper.toEntity(category));
    }

    @Override
    public CategoryEntity save(String id, Category category) {
        log.info("Performing CategoryService create");
        if (StringUtils.isEmpty(category.getName())) {
            throw new InvalidDataException(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        Optional<CategoryEntity> existingCategoryEntityOpt = categoryRepository.findById(id);
        if (existingCategoryEntityOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("Category with id [%s] is not found", id));
        }
        CategoryEntity existingCategoryEntity = existingCategoryEntityOpt.get();
        if (existsByName(category.getName())) {
            throw new InvalidDataException(DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        if (category.isDefault()) {
            CategoryEntity defaultCategoryEntity = categoryRepository.findDefaultCategory();
            if (defaultCategoryEntity != null && !defaultCategoryEntity.getId().equals(id)) {
                throw new InvalidDataException(DEFAULT_CATEGORY_SINGLETON_ERROR_MESSAGE);
            }
        }
        String parentCategoryId = category.getParentCategoryId();
        if (StringUtils.isNotEmpty(parentCategoryId)
                && !parentCategoryId.equals(existingCategoryEntity.getParentCategoryId())) {
            CategoryEntity parentCategory;
            try {
                parentCategory = this.findById(category.getParentCategoryId());
            } catch (ItemNotFoundException ex) {
                throw new UnprocessableContentException(NON_EXISTENT_PARENT_CATEGORY_ERROR_MESSAGE);
            }
            if (StringUtils.isNotEmpty(parentCategory.getParentCategoryId())) {
                throw new InvalidDataException(SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE);
            }
        }
        mapper.updateFields(existingCategoryEntity, category);
        return super.save(existingCategoryEntity);
    }

    @Override
    public boolean existsById(String id) {
        log.info("Performing CategoryService existsById");
        return this.categoryRepository.existsById(id);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        log.info("Performing CategoryService deleteById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException(REQUIRED_CATEGORY_ID_ERROR_MESSAGE);
        }
        CategoryEntity existingCategory = this.findById(id);
        if (existingCategory.isDefault()) {
            throw new InvalidDataException(DEFAULT_CATEGORY_CANNOT_BE_REMOVED_ERROR_MESSAGE);
        }
        categoryRepository.detachChildCategoriesFromParent(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryEntity findDefaultCategory() {
        log.info("Performing CategoryService findDefaultCategory");
        CategoryEntity defaultCategory = categoryRepository.findDefaultCategory();
        if (defaultCategory == null) {
            throw new ItemNotFoundException("There is no default category at this time");
        }
        return defaultCategory;
    }

    @Override
    public boolean existsByName(String name) {
        log.info("Performing CategoryService existsByName");
        return categoryRepository.existsByName(name);
    }
}
