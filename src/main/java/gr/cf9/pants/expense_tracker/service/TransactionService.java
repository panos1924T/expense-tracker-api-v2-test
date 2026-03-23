package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.*;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionCreateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionUpdateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransferCreateDTO;
import gr.cf9.pants.expense_tracker.mapper.TransactionMapper;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.Transaction;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
import gr.cf9.pants.expense_tracker.repository.TransactionRepository;
import gr.cf9.pants.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {

    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public TransactionReadOnlyDTO createTransaction(TransactionCreateDTO dto, UUID userUuid) throws InvalidTransactionException, InsufficientBalanceException {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userUuid + " not found!"));

        Account sourceAccount = accountRepository.findByIdAndUser(dto.sourceAccountId(), user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to account with id " + dto.sourceAccountId()));

        if (dto.type() == TransactionType.TRANSFER) {
            throw new InvalidTransactionException("Use /transfer endpoint for transfers");
        }

        if (dto.categoryId() == null) {
            throw new ValidationException("Category is required for non-transfer transactions");
        }

        Category category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id: " + dto.categoryId() + " not found!"));

        if (dto.type() == TransactionType.EXPENSE) {
            if (sourceAccount.getBalance().compareTo(dto.amount()) < 0) {
                throw new InsufficientBalanceException("Insufficient balance!");
            }
        }

        //PREPARE
        Transaction transaction = transactionMapper.toEntity(dto, user, sourceAccount, category);
        BigDecimal newBalance;
        if (dto.type() == TransactionType.INCOME) {
            newBalance = sourceAccount.getBalance().add(dto.amount());
        } else {
            newBalance = sourceAccount.getBalance().subtract(dto.amount());
        }

        //EXECUTE
        sourceAccount.setBalance(newBalance);
        transactionRepository.save(transaction);


        //RETURN
        return transactionMapper.toReadOnly(transaction);
    }

    @Transactional
    @Override
    public TransactionReadOnlyDTO createTransfer(TransferCreateDTO dto, UUID userUuid) throws InsufficientBalanceException {
        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with id: " + userUuid + " not found!"));

        Account sourceAccount = accountRepository.findByIdAndUser(dto.sourceAccountId(), user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to account with id " + dto.sourceAccountId()));

        Account targetAccount = accountRepository.findByIdAndUser(dto.targetAccountId(), user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to account with id " + dto.targetAccountId()));

        if (sourceAccount.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance!");
        }

        //PREPARE
        Transaction transaction = transactionMapper.toEntity(dto, user, sourceAccount, targetAccount);
        BigDecimal newSourceBalance;
        BigDecimal newTargetBalance;

        newSourceBalance = sourceAccount.getBalance().subtract(dto.amount());
        newTargetBalance = targetAccount.getBalance().add(dto.amount());

        //EXECUTE
        sourceAccount.setBalance(newSourceBalance);
        targetAccount.setBalance(newTargetBalance);
        transactionRepository.save(transaction);

        //RETURN
        return transactionMapper.toReadOnly(transaction);
    }

    @Override
    public TransactionReadOnlyDTO getTransaction(Long id, UUID userUuid) {
        //VALIDATE

        //PREPARE

        //EXECUTE

        //RETURN
        return null;
    }

    @Override
    public TransactionReadOnlyDTO updateTransaction(Long id, TransactionUpdateDTO dto, UUID userUuid) {
        //VALIDATE

        //PREPARE

        //EXECUTE

        //RETURN
        return null;
    }

    @Override
    public List<TransactionReadOnlyDTO> getAllTransactions(UUID userUuid, Pageable pageable) {
        //VALIDATE

        //PREPARE

        //EXECUTE

        //RETURN
        return List.of();
    }

    @Override
    public List<TransactionReadOnlyDTO> getTransactionByAccount(Long accountId, UUID userUuid, Pageable pageable) {
        //VALIDATE

        //PREPARE

        //EXECUTE

        //RETURN
        return List.of();
    }

    @Override
    public List<TransactionReadOnlyDTO> getTransactionByType(TransactionType type, UUID userUuid, Pageable pageable) {
        //VALIDATE

        //PREPARE

        //EXECUTE

        //RETURN
        return List.of();
    }

    @Override
    public void deleteTransaction(Long id, UUID userUuid) {
        //VALIDATE

        //PREPARE

        //EXECUTE

    }
}
