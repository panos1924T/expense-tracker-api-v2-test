package gr.cf9.pants.expense_tracker.mapper;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionCreateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransferCreateDTO;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.Transaction;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionReadOnlyDTO toReadOnly (Transaction transaction) {
        return new TransactionReadOnlyDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getSourceAccount().getId(),
                transaction.getSourceAccount().getName(),

                transaction.getTargetAccount() != null ? transaction.getTargetAccount().getId() : null,
                transaction.getTargetAccount() != null ? transaction.getTargetAccount().getName() : null,

                transaction.getCategory() != null ? transaction.getCategory().getId() : null,
                transaction.getCategory() != null ? transaction.getCategory().getName() : null,

                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    public Transaction toEntity(TransactionCreateDTO dto, User user, Account sourceAccount, Category category) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSourceAccount(sourceAccount);
        transaction.setCategory(category);
        transaction.setType(dto.type());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        return transaction;
    }

    public Transaction toEntity(TransferCreateDTO dto, User user, Account sourceAccount, Account targetAccount) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
        transaction.setType(TransactionType.TRANSFER);
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        return transaction;
    }
}
