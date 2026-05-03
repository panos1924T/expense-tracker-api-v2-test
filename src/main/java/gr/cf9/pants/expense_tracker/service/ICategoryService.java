package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;

import java.util.List;
import java.util.UUID;

public interface ICategoryService {

    CategoryReadOnlyDTO createCategory(CategoryCreateDTO dto, UUID userUuid);

    CategoryReadOnlyDTO updateCategory(UUID categoryUuid, CategoryUpdateDTO dto, UUID userUuid);

    void deleteCategory(UUID categoryUuid, UUID userUuid);

    CategoryReadOnlyDTO getCategoryByUuid(UUID categoryUuid, UUID userUuid);

    List<CategoryReadOnlyDTO> getAllCategories(UUID userUuid);

    List<CategoryReadOnlyDTO> getCategoryByType(TransactionType type, UUID userUuid);
}
