package gr.cf9.pants.expense_tracker.validator;

import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryUpdateValidator {

    private final CategoryRepository categoryRepository;

    public void validate(UUID categoryUuid, CategoryUpdateDTO dto, Errors errors) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var catOpt = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user);
        if (catOpt.isEmpty()) {
            errors.rejectValue("name", "category.notFound", "Category with uuid=" + categoryUuid + " not found");
            return;
        }
        Category category = catOpt.get();
        String newName = dto.name().trim().toLowerCase();

        if (category.getParent() == null) {
            if (categoryRepository.existsCategoryByUserAndNameAndTypeAndParentIsNullAndUuidNot(user, newName, category.getType(), categoryUuid)) {
                errors.rejectValue("name", "category.exists", "Parent category already exists");
            }
        } else {
            if (categoryRepository.existsCategoryByUserAndNameAndTypeAndParentAndUuidNot(user, newName, category.getType(), category.getParent(), categoryUuid)) {
                errors.rejectValue("name", "category.exists", "Child category already exists under this parent");
            }
        }
    }
}