package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITransactionService {

        TransactionReadOnlyDTO createTransaction(TransactionCreateDTO dto, UUID userUuid);

        TransactionReadOnlyDTO createTransfer(TransferCreateDTO dto, UUID userUuid);

        TransactionReadOnlyDTO updateTransaction(UUID transUuid, TransactionUpdateDTO dto, UUID userUuid);

        TransactionReadOnlyDTO updateTransfer(UUID transUuid, TransferUpdateDTO dto, UUID userUuid);

        TransactionReadOnlyDTO getTransactionByUuid(UUID transUuid, UUID userUuid);

        List<TransactionReadOnlyDTO> getTransactionByActiveAccount(UUID accountUuid, UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getTransactionByAccount(UUID accountUuid, UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getTransactionByType(TransactionType type, UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getTransactionByActiveCategory(UUID categoryUuid, UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getTransactionByCategory(UUID categoryUuid, UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getAllTransactions(UUID userUuid, Pageable pageable);

        void deleteTransaction(UUID transUuid, UUID userUuid);
}
