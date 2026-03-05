package gr.cf9.pants.expense_tracker.dto.category_dto;

import gr.cf9.pants.expense_tracker.model.Category;

public record CategoryUpdateDTO(
        String name,
        Long parentId
) {}
