package gr.cf9.pants.expense_tracker.validator;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.core.exceptions.InvalidArgumentException;
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

        if (dto.type() == null) throw new InvalidArgumentException("TransactionType", "Transaction type is required");
        switch (dto.type()) {
            case INCOME -> {
                if (dto.categoryUuid() == null) {
                    errors.rejectValue("categoryUuid", "field.required", "Category is required for transaction type INCOME");
                } else {
                    Category catOpt = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(dto.categoryUuid(), user)
                            .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid=" + dto.categoryUuid() + " not found"));
                    if (catOpt.getType() != TransactionType.INCOME) {
                        errors.rejectValue("categoryUuid", "category.invalid", "Category must be type INCOME");
                    }
                }
                if (!accountRepository.existsAccountByUserAndDefaultAccountTrue(user)) {
                    errors.rejectValue("sourceAccountUuid", "account.notFound", "Default account not found");
                }
            }
            case EXPENSE -> {
                if (dto.categoryUuid() == null) {
                    errors.rejectValue("categoryUuid", "field.required", "Category is required for transaction type EXPENSE");
                } else {
                    Category catOpt = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(dto.categoryUuid(), user)
                            .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid=" + dto.categoryUuid() + " not found"));
                    if (catOpt.getType() != TransactionType.EXPENSE) {
                        errors.rejectValue("categoryUuid", "category.invalid", "Category must be type EXPENSE");
                    }
                }
                if (dto.sourceAccountUuid() == null) {
                    errors.rejectValue("sourceAccountUuid", "field.required", "Source account is required");
                } else if (!accountRepository.existsAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)) {
                    errors.rejectValue("sourceAccountUuid", "account.notFound", "Source account with uuid=" + dto.sourceAccountUuid() + " not found");
                }
            }
            case TRANSFER -> {
                if (dto.sourceAccountUuid() == null) errors.rejectValue("sourceAccountUuid", "field.required", "Source account is required for transaction type TRANSFER");
                if (dto.targetAccountUuid() == null) errors.rejectValue("targetAccountUuid", "field.required", "Target account is required for transaction type TRANSFER");


                if (dto.sourceAccountUuid().equals(dto.targetAccountUuid())) {
                        errors.rejectValue("targetAccountUuid", "transfer.sameAccount", "Source and Target account cannot be the same");
                }
            }
        }
    }
}