package gr.cf9.pants.expense_tracker.mapper;

import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.stereotype.Component;


@Component
public class CategoryMapper {

    public CategoryReadOnlyDTO toReadOnly(Category category) {
        return new CategoryReadOnlyDTO(
                category.getId(),
                category.getName(),
                category.getType(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getParent() != null ? category.getParent().getName() : null
        );
    }

    public Category toEntity(CategoryCreateDTO dto, User user) {
        Category category = new Category();
        category.setUser(user);
        category.setName(dto.name());
        category.setType(dto.type());
        return category;
    }
}
