package gr.cf9.pants.expense_tracker.validator;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionCreateDTO;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionInsertValidator implements Validator {

    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    @Override public boolean supports(Class<?> clazz) { return TransactionCreateDTO.class.equals(clazz); }

    @Override
    public void validate(Object target, Errors errors) {
        TransactionCreateDTO dto = (TransactionCreateDTO) target;
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (dto.type() == null) {
            errors.rejectValue("type", "field.required", "Transaction type is required");
            return;
        }

        switch (dto.type()) {
            case INCOME -> validateIncomeInsert(dto, user, errors);
            case EXPENSE -> validateExpenseInsert(dto, user, errors);
            case TRANSFER -> validateTransferInsert(dto, user, errors);
        }
    }

    private void validateIncomeInsert(TransactionCreateDTO dto, User user, Errors errors) {
        validateCategoryExistenceAndType(dto.categoryUuid(), TransactionType.INCOME, user, errors);
        if (!accountRepository.existsAccountByUserAndDefaultAccountTrue(user)) {
            errors.rejectValue("sourceAccountUuid", "account.notFound", "Default account not found");
        }
    }

    private void validateExpenseInsert(TransactionCreateDTO dto, User user, Errors errors) {
        validateCategoryExistenceAndType(dto.categoryUuid(), TransactionType.EXPENSE, user, errors);
        validateAccountExistence("sourceAccountUuid", dto.sourceAccountUuid(), user, errors);
    }

    private void validateTransferInsert(TransactionCreateDTO dto, User user, Errors errors) {
        boolean isSourceAccountValid = validateAccountExistence("sourceAccountUuid", dto.sourceAccountUuid(), user, errors);
        boolean isTargetAccountValid = validateAccountExistence("targetAccountUuid", dto.targetAccountUuid(), user, errors);

        if (isSourceAccountValid && isTargetAccountValid && dto.sourceAccountUuid().equals(dto.targetAccountUuid())) {
            errors.rejectValue("targetAccountUuid", "transfer.sameAccount", "Source and Target account cannot be the same");
        }
    }

    // --- REUSABLE HELPERS ---

    private void validateCategoryExistenceAndType(UUID categoryUuid, TransactionType expectedType, User user, Errors errors) {
        if (categoryUuid == null) {
            errors.rejectValue("categoryUuid", "field.required", "Category is required for transaction type " + expectedType);
            return;
        }
        Optional<Category> categoryOptional = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user);
        if (categoryOptional.isEmpty()) {
            errors.rejectValue("categoryUuid", "category.notFound", "Category with uuid=" + categoryUuid + " not found");
        } else if (categoryOptional.get().getType() != expectedType) {
            errors.rejectValue("categoryUuid", "category.invalid", "Category must be type " + expectedType);
        }
    }

    private boolean validateAccountExistence(String fieldName, UUID accountUuid, User user, Errors errors) {
        if (accountUuid == null) {
            errors.rejectValue(fieldName, "field.required", fieldName + " is required");
            return false;
        }
        if (!accountRepository.existsAccountByUuidAndUserAndDeletedFalse(accountUuid, user)) {
            errors.rejectValue(fieldName, "account.notFound", "Account with uuid=" + accountUuid + " not found");
            return false;
        }
        return true;
    }
}