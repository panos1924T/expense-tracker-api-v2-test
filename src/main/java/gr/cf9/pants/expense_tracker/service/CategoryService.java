package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityAlreadyExistsException;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.core.exceptions.InvalidArgumentException;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.CategoryMapper;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
import gr.cf9.pants.expense_tracker.repository.TransactionRepository;
import gr.cf9.pants.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService implements ICategoryService{

    private final CategoryMapper categoryMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public CategoryReadOnlyDTO createCategory(CategoryCreateDTO dto, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        String name = normalizeName(dto.name());

        if (dto.type() == null) {
            throw new InvalidArgumentException("Category", "Category type is required");
        }

        Category category = categoryMapper.toEntity(dto, user);
        category.setName(name);

        if (dto.parentUuid() == null) {
            if (categoryRepository.existsCategoryByUserAndNameAndTypeAndParentIsNullAndDeletedFalse(
                    user,
                    dto.type(),
                    name
            )) {
                throw new EntityAlreadyExistsException("Category", "Root category already exists");
            }

            category.setParent(null);
        } else {
            Category parent = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(dto.parentUuid(), user)
                    .orElseThrow(() -> new EntityNotFoundException("Category", "Parent category with uuid: " + dto.parentUuid() + " not found!"));

            if (parent.getParent() != null) {
                throw new InvalidArgumentException("Category", "Child category cannot be used as parent");
            }

            if (parent.getType() != dto.type()) {
                throw new InvalidArgumentException("Category", "Child category type must match parent category type");
            }

            if (categoryRepository.existsCategoryByUserAndNameAndTypeAndParentAndDeletedFalse(
                    user,
                    dto.type(),
                    name,
                    parent
            )) {
                throw new EntityAlreadyExistsException("Category", "Child category already exists under this parent");
            }

            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);

        //RETURN
        return categoryMapper.toReadOnly(savedCategory);
    }

    @Transactional
    @Override
    public CategoryReadOnlyDTO updateCategory(UUID categoryUuid, CategoryUpdateDTO dto, UUID userUuid) {

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Category category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + "not found!"));

        String name = normalizeName(dto.name());

        if (category.getParent() == null) {
            if (!category.getName().equalsIgnoreCase(name)
                    && categoryRepository.existsCategoryByUserAndNameAndTypeAndParentIsNullAndDeletedFalse(
                    user,
                    category.getType(),
                    name
            )) {
                throw new EntityAlreadyExistsException("Category", "Root category already exists");
            }
        } else {
            if (!category.getName().equalsIgnoreCase(name)
                    && categoryRepository.existsCategoryByUserAndNameAndTypeAndParentAndDeletedFalse(
                    user,
                    category.getType(),
                    name,
                    category.getParent()
            )) {
                throw new EntityAlreadyExistsException("Category", "Child category already exists under this parent");
            }
        }

        category.setName(name);

        //EXECUTE
        Category updatedCategory = categoryRepository.save(category);

        //RETURN
        return categoryMapper.toReadOnly(updatedCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(UUID categoryUuid, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Category category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + "not found!"));

        if (category.getParent() == null && categoryRepository.existsCategoryByParentAndDeletedFalse(category)) {
            throw new InvalidArgumentException("Category", "Cannot delete parent category with active children");
        }

        boolean hasTrans = transactionRepository.existsTransByCategory(category);       //TODO Αν έχει softDeleted Children πρέπει να είναι στο root softDelete
        if (hasTrans == true) {
            category.softDelete(Instant.now());
            categoryRepository.save(category);
        } else {
            categoryRepository.delete(category);
        }
    }

    @Override
    public CategoryReadOnlyDTO getCategoryByUuid(UUID categoryUuid, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Category category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + "not found!"));

        //RETURN
        return categoryMapper.toReadOnly(category);
    }

    @Override
    public List<CategoryReadOnlyDTO> getAllCategories(UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        //PREPARE
        List<Category> categories = categoryRepository.findCategoryByUserAndDeletedFalse(user);

        //EXECUTE & RETURN
        return categories.stream()
                .map(categoryMapper::toReadOnly)
                .toList();
    }

    @Override
    public CategoryReadOnlyDTO getActiveCategory(UUID categoryUuid, UUID userUuid) {        //TODO λάθος, θέλω να υλοποιήσω να φέρνει όλα τα categories όχι active το κάνω από πάνω
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        Category category = categoryRepository.findCategoryByUuidAndUser(categoryUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + "not found!"));

        return categoryMapper.toReadOnly(category);
    }

    @Override
    public List<CategoryReadOnlyDTO> getCategoryByType(TransactionType type, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        //PREPARE
        List<Category> categories = categoryRepository.findCategoryByUserAndTypeAndDeletedFalse(user, type);

        //EXECUTE & RETURN
        return categories.stream()
                .map(categoryMapper::toReadOnly)
                .toList();
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidArgumentException("Category", "Category name is required");
        }
        return name.trim().toLowerCase();
    }
}
