package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.CategoryRequest;
import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl extends DataBaseService<Category, CategoryEntity> implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CommonValidator commonValidator;
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
    public Category findById(String id) {
        log.info("Performing CategoryService findById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException(REQUIRED_CATEGORY_ID_ERROR_MESSAGE);
        }
        return DaoUtils.toData(categoryRepository.findById(id));
    }

    @Override
    public Category findByName(String name) {
        log.info("Performing CategoryService findByName");
        if (StringUtils.isEmpty(name)) {
            throw new InvalidDataException(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        return DaoUtils.toData(categoryRepository.findByName(name));
    }

    @Override
    public PageData<Category> findCategories(PageParameter pageParameter) {
        log.info("Performing CategoryService find");
        commonValidator.validatePageParameter(pageParameter);
        return DaoUtils.toPageData(categoryRepository.findParentCategories(DaoUtils.toPageable(pageParameter)));
    }

    @Override
    @Transactional
    public Category create(CategoryRequest categoryRequest) {
        log.info("Performing CategoryService create");
        if (StringUtils.isEmpty(categoryRequest.getName())) {
            throw new InvalidDataException(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        Category nameDuplicatedCategory = this.findByName(categoryRequest.getName());
        if (nameDuplicatedCategory != null) {
            throw new InvalidDataException(DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        if (categoryRequest.isDefault()) {
            CategoryEntity defaultCategoryEntity = categoryRepository.findDefaultCategory();
            if (defaultCategoryEntity != null) {
                throw new InvalidDataException(DEFAULT_CATEGORY_SINGLETON_ERROR_MESSAGE);
            }
        }
        if (StringUtils.isNotEmpty(categoryRequest.getParentCategoryId())) {
            Category parentCategory = this.findById(categoryRequest.getParentCategoryId());
            if (parentCategory == null) {
                throw new UnprocessableContentException(NON_EXISTENT_PARENT_CATEGORY_ERROR_MESSAGE);
            }
            if (StringUtils.isNotEmpty(parentCategory.getParentCategoryId())) {
                throw new InvalidDataException(SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE);
            }
        }
        Category newCategory = new Category(categoryRequest);
        newCategory = super.insert(newCategory);
        return newCategory;
    }

    @Override
    public Category save(String id, CategoryRequest categoryRequest) {
        log.info("Performing CategoryService create");
        if (StringUtils.isEmpty(categoryRequest.getName())) {
            throw new InvalidDataException(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        Category existingCategory = findById(id);
        if (existingCategory == null) {
            throw new ItemNotFoundException(String.format("Category with id [%s] is not found", id));
        }
        Category nameDuplicatedCategory = this.findByName(categoryRequest.getName());
        if (nameDuplicatedCategory != null && !nameDuplicatedCategory.getId().equals(id)) {
            throw new InvalidDataException(DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE);
        }
        if (categoryRequest.isDefault()) {
            CategoryEntity defaultCategoryEntity = categoryRepository.findDefaultCategory();
            if (defaultCategoryEntity != null && !defaultCategoryEntity.getId().equals(id)) {
                throw new InvalidDataException(DEFAULT_CATEGORY_SINGLETON_ERROR_MESSAGE);
            }
        }
        Category savedCategory = null;
        String parentCategoryId = categoryRequest.getParentCategoryId();
        if (StringUtils.isNotEmpty(parentCategoryId)
                && !parentCategoryId.equals(existingCategory.getParentCategoryId())) {
            Category parentCategory = this.findById(parentCategoryId);
            if (parentCategory == null) {
                throw new UnprocessableContentException(NON_EXISTENT_PARENT_CATEGORY_ERROR_MESSAGE);
            }
            if (StringUtils.isNotEmpty(parentCategory.getParentCategoryId())) {
                throw new InvalidDataException(SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE);
            }
        }
        Category category = new Category(categoryRequest);
        category.setId(id);
        savedCategory = super.save(category);
        return savedCategory;
    }

    @Override
    public boolean existsById(String id) {
        log.info("Performing CategoryService existsById");
        return this.categoryRepository.existsById(id);
    }


    //DEFAULT CATEGORY
    @Override
    @Transactional
    public void deleteById(String id) {
        log.info("Performing CategoryService deleteById");
        if (StringUtils.isEmpty(id)) {
            throw new InvalidDataException(REQUIRED_CATEGORY_ID_ERROR_MESSAGE);
        }
        Category existingCategory = this.findById(id);
        if (existingCategory == null) {
            throw new ItemNotFoundException(String.format("Category with id [%s] is not found", id));
        }
        if (existingCategory.isDefault()) {
            throw new InvalidDataException(DEFAULT_CATEGORY_CANNOT_BE_REMOVED_ERROR_MESSAGE);
        }
        categoryRepository.detachChildCategoriesFromParent(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public Category findDefaultCategory() {
        log.info("Performing CategoryService findDefaultCategory");
        return DaoUtils.toData(categoryRepository.findDefaultCategory());
    }

//    private Category getOrCreateDefaultCategory() {
//        log.info("Performing CategoryService getOrCreateDefaultCategory");
//        Category defaultCategory = DaoUtils.toData(categoryRepository.findDefaultCategory());
//        if (defaultCategory == null) {
//            //create new default category
//            defaultCategory = new Category();
//            defaultCategory.setName(CATEGORY_DEFAULT_CATEGORY_NAME);
//            defaultCategory.setParentCategoryId(null);
//            defaultCategory = this.create(defaultCategory);
//        }
//        return defaultCategory;
//    }
}