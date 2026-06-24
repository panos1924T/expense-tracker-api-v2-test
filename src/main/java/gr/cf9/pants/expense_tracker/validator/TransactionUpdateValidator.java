package gr.cf9.pants.expense_tracker.validator;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionUpdateDTO;
import gr.cf9.pants.expense_tracker.model.Transaction;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
import gr.cf9.pants.expense_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionUpdateValidator {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public void validate(UUID transUuid, TransactionUpdateDTO dto, Errors errors) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        var transactionOptional = transactionRepository.findTransByUuidAndUser(transUuid, user);
        if (transactionOptional.isEmpty()) {
            errors.rejectValue("amount", "transactionOptional.notFound", "Transaction with uuid=" + transUuid + " not found");
            return;
        }
        Transaction transaction = transactionOptional.get();

        if (transaction.getType() == TransactionType.TRANSFER) {
            if (dto.sourceAccountUuid() == null || dto.targetAccountUuid() == null) {
                errors.rejectValue("sourceAccountUuid", "field.required", "Both source and target accounts are required");
                return;
            }
            if (dto.sourceAccountUuid().equals(dto.targetAccountUuid())) {
                errors.rejectValue("targetAccountUuid", "transfer.same", "Source and Target account cannot be the same");
            }
            if (!dto.sourceAccountUuid().equals(transaction.getSourceAccount().getUuid()) && !accountRepository.existsAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)) {
                errors.rejectValue("sourceAccountUuid", "account.notFound", "Updated account with uuid=" + transaction.getSourceAccount().getUuid() + " not found");
            }
            if (!dto.targetAccountUuid().equals(transaction.getTargetAccount().getUuid()) && !accountRepository.existsAccountByUuidAndUserAndDeletedFalse(dto.targetAccountUuid(), user)) {
                errors.rejectValue("targetAccountUuid", "account.notFound", "Updated account with uuid=" + transaction.getTargetAccount().getUuid() + " not found");
            }
        } else {
            if (dto.categoryUuid() == null) {
                errors.rejectValue("categoryUuid", "field.required", "Category is required");
            } else if (!dto.categoryUuid().equals(transaction.getCategory().getUuid())) {
                var categoryOptional = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(dto.categoryUuid(), user);
                if (categoryOptional.isEmpty() || categoryOptional.get().getType() != transaction.getType()) {
                    errors.rejectValue("categoryUuid", "category.invalid", "Category type must match transaction type " + transaction.getType());
                }
            }

            if (transaction.getType() == TransactionType.EXPENSE) {
                if (dto.sourceAccountUuid() == null) {
                    errors.rejectValue("sourceAccountUuid", "field.required", "Source account is required for transaction type EXPENSE");
                } else if (!dto.sourceAccountUuid().equals(transaction.getSourceAccount().getUuid()) && !accountRepository.existsAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)) {
                    errors.rejectValue("sourceAccountUuid", "account.notFound", "Updated source account with uuid=" + transaction.getSourceAccount().getUuid() + " not found");
                }
            }
        }
    }
}