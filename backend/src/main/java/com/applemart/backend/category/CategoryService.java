package com.applemart.backend.category;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO createCategory(CategoryDTO request);
    CategoryDTO updateCategory(Integer id, CategoryDTO request);
    void deleteCategoryByUrlKey(String urlKey);
}
