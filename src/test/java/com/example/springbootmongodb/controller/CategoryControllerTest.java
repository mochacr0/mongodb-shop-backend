package com.example.springbootmongodb.controller;

import com.example.springbootmongodb.common.data.Category;
import com.example.springbootmongodb.common.data.CategoryRequest;
import com.example.springbootmongodb.common.data.User;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import static com.example.springbootmongodb.controller.ControllerConstants.*;
import static com.example.springbootmongodb.service.CategoryServiceImpl.*;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CategoryControllerTest extends AbstractControllerTest {
    private User user;
    @BeforeAll
    void setUp() throws Exception {
        user = createUser(generateUsername(), generateEmail(), DEFAULT_PASSWORD, DEFAULT_PASSWORD);
        activateUser(user.getId());
        login(user.getName(), DEFAULT_PASSWORD);
    }

    @AfterAll
    void tearDown() throws Exception {
        if (user != null && StringUtils.isNotEmpty(user.getId())) {
            User existingUser = performGet(USERS_GET_USER_BY_ID_ROUTE, User.class, user.getId());
            if (existingUser != null) {
                deleteUser(user.getId());
            }
        }
    }
    @Test
    void testGetDefaultCategory() throws Exception {
        Category defaultCategory = performGet(CATEGORY_GET_DEFAULT_CATEGORY_ROUTE, Category.class);
        Assertions.assertNotNull(defaultCategory);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class CreateCategoryMethodTest {
        @Test
        void testCreateCategoryWithInvalidName() throws Exception {
            //missing name
            CategoryRequest categoryRequest = createCategoryRequest();
            categoryRequest.setName(null);
            performPost(CATEGORY_CREATE_CATEGORY_ROUTE, categoryRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE)));
            categoryRequest.setName("");
            performPost(CATEGORY_CREATE_CATEGORY_ROUTE, categoryRequest)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE)));
            //duplicated name
            Category defaultCategory = getDefaultCategory();
            CategoryRequest newCategory = new CategoryRequest();
            newCategory.setName(defaultCategory.getName());
            performPost(CATEGORY_CREATE_CATEGORY_ROUTE, newCategory)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE)));
        }

        @Test
        void testCreateCategoryWithNonExistentParentId() throws Exception {
            CategoryRequest categoryRequest = createCategoryRequest();
            categoryRequest.setParentCategoryId(NON_EXISTENT_ID);
            performPost(CATEGORY_CREATE_CATEGORY_ROUTE, categoryRequest)
                    .andExpect(status().isUnprocessableEntity());
        }

//        @Test
        void testCreateCategoryWithSubcategoryParent() throws Exception {
            //setup: create parent category and subcategory
            Category parentCategory = createCategory();
            Category subcategory = createCategory(new CategoryRequest(generateRandomString(), parentCategory.getId(), false));
            //create invalid subcategory
            Category invalidSubCategory = new Category(generateRandomString(), subcategory.getId());
            performPost(CATEGORY_CREATE_CATEGORY_ROUTE, invalidSubCategory)
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE)));
            deleteCategory(parentCategory.getId());
            deleteCategory(subcategory.getId());
        }

        @Test
        void testCreateCategoryHierarchyWithValidBody() throws Exception {
            Category parentCategory = createCategory();
            Category subcategory = createCategory(new CategoryRequest(generateRandomString(), parentCategory.getId(), false));
            Assertions.assertEquals(subcategory.getParentCategoryId(), parentCategory.getId());
            parentCategory = performGet(CATEGORY_GET_CATEGORY_BY_ID_ROUTE, Category.class, parentCategory.getId());
            Assertions.assertEquals(parentCategory.getSubCategories().get(0).getId(), subcategory.getId());
            deleteCategory(parentCategory.getId());
            deleteCategory(subcategory.getId());
        }

    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UpdateCategoryMethodTest {
        private Category category;

        @BeforeEach
        void setUp() throws Exception {
            category = createCategory();
        }

        @AfterEach
        void tearDown() throws Exception {
            if (category != null && StringUtils.isNotEmpty(category.getId())) {
                category = performGet(CATEGORY_GET_CATEGORY_BY_ID_ROUTE, Category.class, category.getId());
                if (category != null) {
                    deleteCategory(category.getId());
                }
            }
         }

        @Test
        void testUpdateCategoryWithNonExistentId() throws Exception {
            CategoryRequest categoryRequest = new CategoryRequest(generateRandomString(), category.getParentCategoryId());
            performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, categoryRequest, NON_EXISTENT_ID)
                    .andExpect(status().isNotFound());
        }

        @Test
        void testUpdateCategoryWithInvalidName() throws Exception {
            Category defaultCategory = getDefaultCategory();
            CategoryRequest categoryRequest = createCategoryRequest();
            categoryRequest.setName(null);
            performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, categoryRequest, category.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE)));
            categoryRequest.setName("");
            performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, categoryRequest, category.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(REQUIRED_CATEGORY_NAME_ERROR_MESSAGE)));
            categoryRequest.setName(defaultCategory.getName());
            performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, categoryRequest, category.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DUPLICATED_CATEGORY_NAME_ERROR_MESSAGE)));
        }

        //invalid parent id
            //non-existent parent id
            //hierarchy violation
        @Test
        void testUpdateCategoryWithNonExistentParentId() throws Exception {
            //non-existent parent id
            String nonExistentParentId = "64805c5bdb4a3449c81a9bed";
            performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, new CategoryRequest(generateRandomString(), nonExistentParentId), category.getId())
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.message", is(NON_EXISTENT_PARENT_CATEGORY_ERROR_MESSAGE)));
        }

        @Test
        void testUpdateCategoryWithHierarchyViolation() throws Exception {
            Category parentCategory = createCategory();
            Category subcategory = createCategory(new CategoryRequest(generateRandomString(), parentCategory.getId(), false));
            //create invalid subcategory
            performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, new CategoryRequest(category.getName(), subcategory.getId()), category.getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(SUBCATEGORY_HIERARCHY_VIOLATION_ERROR_MESSAGE)));
            deleteCategory(parentCategory.getId());
            deleteCategory(subcategory.getId());
        }

        @Test
        void testUpdateCategoryWithValidBody() throws Exception {
            CategoryRequest categoryRequest = new CategoryRequest(generateRandomString(), null);
            Category updatedCategory = performPut(CATEGORY_UPDATE_CATEGORY_ROUTE, Category.class, categoryRequest, category.getId());
            Assertions.assertEquals(categoryRequest.getName(), updatedCategory.getName());
            Assertions.assertEquals(categoryRequest.getParentCategoryId(), updatedCategory.getParentCategoryId());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class DeleteCategoryMethodTest {
        private Category category;
        @BeforeEach
        void setUp() throws Exception {
            category = createCategory();
        }

        @AfterEach
        void tearDown() throws Exception {
            if (category != null && StringUtils.isNotEmpty(category.getId())) {
                category = performGet(CATEGORY_GET_CATEGORY_BY_ID_ROUTE, Category.class, category.getId());
                if (category != null) {
                    deleteCategory(category.getId());
                }
            }
        }

        //non-existent id
        @Test
        void testDeleteWithNonExistentCategoryId() throws Exception {
            performDelete(CATEGORY_DELETE_CATEGORY_BY_ID_ROUTE, NON_EXISTENT_ID).andExpect(status().isNotFound());
        }

        //deleting default category
        @Test
        void testDeleteDefaultCategory() throws Exception {
            performDelete(CATEGORY_DELETE_CATEGORY_BY_ID_ROUTE, getDefaultCategory().getId())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message", is(DEFAULT_CATEGORY_CANNOT_BE_REMOVED_ERROR_MESSAGE)));
        }

        //check subcategory parent id after deleting
        @Test
        void testDeleteCategoryAndDetachSubcategories() throws Exception {
            Category subcategory1 = createCategory(new CategoryRequest(generateRandomString(), category.getId()));
            Category subcategory2 = createCategory(new CategoryRequest(generateRandomString(), category.getId()));
            Assertions.assertEquals(subcategory1.getParentCategoryId(), category.getId());
            Assertions.assertEquals(subcategory2.getParentCategoryId(), category.getId());
            category = performGet(CATEGORY_GET_CATEGORY_BY_ID_ROUTE, Category.class, category.getId());
            Assertions.assertEquals(category.getSubCategories().get(0).getId(), subcategory1.getId());
            Assertions.assertEquals(category.getSubCategories().get(1).getId(), subcategory2.getId());
            performDelete(CATEGORY_DELETE_CATEGORY_BY_ID_ROUTE, category.getId()).andExpect(status().isOk());
            performGet(CATEGORY_GET_CATEGORY_BY_ID_ROUTE, subcategory1.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.parentCategoryId", is(emptyOrNullString())));
            performGet(CATEGORY_GET_CATEGORY_BY_ID_ROUTE, subcategory2.getId())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.parentCategoryId", is(emptyOrNullString())));
            deleteCategory(subcategory1.getId());
            deleteCategory(subcategory2.getId());
        }
    }

    private Category getDefaultCategory() throws Exception {
        return performGet(CATEGORY_GET_DEFAULT_CATEGORY_ROUTE, Category.class);
    }

    private CategoryRequest createCategoryRequest() {
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(generateRandomString());
        categoryRequest.setDefault(false);
        categoryRequest.setParentCategoryId(null);
        return categoryRequest;
    }

    private Category createCategory() throws Exception {
        return createCategory(createCategoryRequest());
    }

    private Category createCategory(CategoryRequest categoryRequest) throws Exception {
        return performPost(CATEGORY_CREATE_CATEGORY_ROUTE, Category.class, categoryRequest);
    }

    private void deleteCategory(String categoryId) throws Exception {
        performDelete(CATEGORY_DELETE_CATEGORY_BY_ID_ROUTE, categoryId);
    }
}
