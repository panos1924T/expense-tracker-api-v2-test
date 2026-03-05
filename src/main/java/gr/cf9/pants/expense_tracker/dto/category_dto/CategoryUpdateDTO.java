package gr.cf9.pants.expense_tracker.dto.category_dto;

public record CategoryUpdateDTO(
        String name,
        Long parentId
) {}
