package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.InsufficientBalanceException;
import gr.cf9.pants.expense_tracker.core.exceptions.InvalidTransactionException;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionCreateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionUpdateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransferCreateDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ITransactionService {

        TransactionReadOnlyDTO createTransaction(TransactionCreateDTO dto, UUID userUuid) throws InvalidTransactionException, InsufficientBalanceException;

        TransactionReadOnlyDTO createTransfer(TransferCreateDTO dto, UUID userUuid) throws InsufficientBalanceException;

        TransactionReadOnlyDTO getTransaction(UUID transUuid, UUID userUuid);

        TransactionReadOnlyDTO updateTransaction(UUID transUuid, TransactionUpdateDTO dto, UUID userUuid);

        List<TransactionReadOnlyDTO> getAllTransactions(UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getTransactionByAccount(UUID accountUuid, UUID userUuid, Pageable pageable);

        List<TransactionReadOnlyDTO> getTransactionByType(TransactionType type, UUID userUuid, Pageable pageable);

        void deleteTransaction(UUID transUuid, UUID userUuid) throws InvalidTransactionException;
}
