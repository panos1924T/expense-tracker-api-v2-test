package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;

import java.util.UUID;

public interface ICategoryInterface {

    CategoryReadOnlyDTO createCategory(CategoryCreateDTO dto, UUID userUuid);

    CategoryReadOnlyDTO updateCategory(CategoryUpdateDTO dto, UUID userUuid);

    void deleteCategory(Long id, UUID userUuid);

}
