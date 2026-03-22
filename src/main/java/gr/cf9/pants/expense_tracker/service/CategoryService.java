package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.core.exceptions.UnauthorizedException;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.CategoryMapper;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
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

    @Transactional
    @Override
    public CategoryReadOnlyDTO createCategory(CategoryCreateDTO dto, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " does not exist!"));

        //PREPARE
        Category category = categoryMapper.toEntity(dto, user);
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category: " + dto.parentId() + " not found!"));
            category.setParent(parent);
        }

        //EXECUTE
        Category savedCategory = categoryRepository.save(category);

        //RETURN
        return categoryMapper.toReadOnly(savedCategory);
    }

    @Transactional
    @Override
    public CategoryReadOnlyDTO updateCategory(Long id, CategoryUpdateDTO dto, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " does not exist!"));
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to category with id: " + id));


        //PREPARE
        if (dto.parentId() != null) {
            Category parent = categoryRepository.findById(dto.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent category: " + dto.parentId() + " not found!"));
            category.setParent(parent);
        }

        category.setName(dto.name());

        //EXECUTE
        Category updatedCategory = categoryRepository.save(category);

        //RETURN
        return categoryMapper.toReadOnly(updatedCategory);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " does not exist!"));
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to category with id: " + id));

        //EXECUTE
        category.softDelete(Instant.now());
        categoryRepository.save(category);
    }

    @Override
    public CategoryReadOnlyDTO getCategoryById(Long id, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " does not exist!"));
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to category with id: " + id));

        //RETURN
        return categoryMapper.toReadOnly(category);
    }

    @Override
    public List<CategoryReadOnlyDTO> getAllCategories(UUID userUuid) {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " does not exist!"));

        //PREPARE
        List<Category> categories = categoryRepository.findByUser(user);

        //EXECUTE & RETURN
        return categories.stream()
                .map(categoryMapper::toReadOnly)
                .toList();
    }

    @Override
    public List<CategoryReadOnlyDTO> getCategoryByType(TransactionType type, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " does not exist!"));

        //PREPARE
        List<Category> categories = categoryRepository.findByUserAndType(user, type);

        //EXECUTE & RETURN
        return categories.stream()
                .map(categoryMapper::toReadOnly)
                .toList();
    }
}
