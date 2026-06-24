package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.ValidationException;
import gr.cf9.pants.expense_tracker.core.filters.CategoryFilters;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryCreateDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.category_dto.CategoryUpdateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionReadOnlyDTO;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.service.ICategoryService;
import gr.cf9.pants.expense_tracker.service.ITransactionService;
import gr.cf9.pants.expense_tracker.validator.CategoryInsertValidator;
import gr.cf9.pants.expense_tracker.validator.CategoryUpdateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final ICategoryService categoryService;
    private final ITransactionService transactionService;
    private final CategoryInsertValidator insertValidator;
    private final CategoryUpdateValidator updateValidator;

    @PostMapping
    public ResponseEntity<CategoryReadOnlyDTO> createCategory(
            @Valid @RequestBody CategoryCreateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User principal) {

        insertValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Category", "Invalid category data", bindingResult);
        }

        CategoryReadOnlyDTO created = categoryService.createCategory(dto, principal.getUuid());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(created.uuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<CategoryReadOnlyDTO> updateCategory(
            @PathVariable UUID uuid,
            @Valid @RequestBody CategoryUpdateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User principal) {

        updateValidator.validate(uuid, dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Category", "Invalid category data", bindingResult);
        }

        return ResponseEntity.ok(categoryService.updateCategory(uuid, dto, principal.getUuid()));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal User principal) {

        categoryService.deleteCategory(uuid, principal.getUuid());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<CategoryReadOnlyDTO>> getCategories(
            @AuthenticationPrincipal User principal,
            @ModelAttribute CategoryFilters filters,
            @PageableDefault(sort = "type", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<CategoryReadOnlyDTO> categoriesPage = categoryService.getFilteredPaginatedCategories(principal, filters, pageable);
        return ResponseEntity.ok(categoriesPage);
    }

//    @GetMapping("/{uuid}")
//    public ResponseEntity<CategoryReadOnlyDTO> getCategory(
//            @PathVariable UUID uuid,
//            @RequestParam(defaultValue = "false") boolean includeDeleted,
//            @AuthenticationPrincipal User principal) {
//
//        CategoryReadOnlyDTO category = includeDeleted ?
//                categoryService.getCategoryByUuid(uuid, principal.getUuid()) :
//                categoryService.getActiveCategoryByUuid(uuid, principal.getUuid());
//        return ResponseEntity.ok(category);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<CategoryReadOnlyDTO>> getCategories(
//            @RequestParam(required = false) TransactionType type,
//            @RequestParam(defaultValue = "false") boolean includeDeleted,
//            @AuthenticationPrincipal User principal) {
//
//        List<CategoryReadOnlyDTO> categories;
//        if (type != null) {
//            categories = includeDeleted ?
//                    categoryService.getCategoriesByType(type, principal.getUuid()) :
//                    categoryService.getActiveCategoriesByType(type, principal.getUuid());
//        } else {
//            categories = includeDeleted ?
//                    categoryService.getAllCategories(principal.getUuid()) :
//                    categoryService.getActiveCategories(principal.getUuid());
//        }
//        return ResponseEntity.ok(categories);
//    }
//
//     NESTED GET: Συναλλαγές που ανήκουν στη συγκεκριμένη κατηγορία
//    @GetMapping("/{uuid}/transactions")
//    public ResponseEntity<List<TransactionReadOnlyDTO>> getCategoryTransactions(
//            @PathVariable UUID uuid,
//            @RequestParam(defaultValue = "false") boolean includeDeleted,
//            @PageableDefault(size = 20) Pageable pageable,
//            @AuthenticationPrincipal User principal) {
//
//        List<TransactionReadOnlyDTO> transactions = includeDeleted ?
//                transactionService.getTransactionByCategory(uuid, principal.getUuid(), pageable) :
//                transactionService.getTransactionByActiveCategory(uuid, principal.getUuid(), pageable);
//        return ResponseEntity.ok(transactions);
//    }
}