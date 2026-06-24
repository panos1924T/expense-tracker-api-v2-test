package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.filters.CategoryFilters;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ICategoryService {

    CategoryReadOnlyDTO createCategory(CategoryCreateDTO dto, UUID userUuid);

    CategoryReadOnlyDTO updateCategory(UUID categoryUuid, CategoryUpdateDTO dto, UUID userUuid);

    void deleteCategory(UUID categoryUuid, UUID userUuid);

//    CategoryReadOnlyDTO getActiveCategoryByUuid(UUID categoryUuid, UUID userUuid);
//
//    CategoryReadOnlyDTO getCategoryByUuid(UUID categoryUuid, UUID userUuid);
//
//    List<CategoryReadOnlyDTO> getActiveCategories(UUID userUuid);
//
//    List<CategoryReadOnlyDTO> getAllCategories(UUID userUuid);
//
//    List<CategoryReadOnlyDTO> getActiveCategoriesByType(TransactionType type, UUID userUuid);
//
//    List<CategoryReadOnlyDTO> getCategoriesByType(TransactionType type, UUID userUuid);

    Page<CategoryReadOnlyDTO> getFilteredPaginatedCategories(User user, CategoryFilters filters, Pageable pageable);
}
