package gr.cf9.pants.expense_tracker.mapper;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionCreateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionUpdateDTO;
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
                transaction.getUuid(),
                transaction.getAmount(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getSourceAccount().getUuid(),
                transaction.getSourceAccount().getName(),

                transaction.getTargetAccount() != null ? transaction.getTargetAccount().getUuid() : null,
                transaction.getTargetAccount() != null ? transaction.getTargetAccount().getName() : null,

                transaction.getCategory() != null ? transaction.getCategory().getUuid() : null,
                transaction.getCategory() != null ? transaction.getCategory().getName() : null,

                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }

    public Transaction toEntity(TransactionCreateDTO dto,User user) {
        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setType(dto.type());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setAmount(dto.amount());
        transaction.setDescription(dto.description());
        return transaction;
    }

    public void updateEntity(Transaction transaction, TransactionUpdateDTO updateDTO) {
        transaction.setTransactionDate(updateDTO.transactionDate());
        transaction.setAmount(updateDTO.amount());
        transaction.setDescription(updateDTO.description());
    }
}
