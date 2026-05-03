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
import java.time.Instant;
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
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));

        Account sourceAccount = accountRepository.findAccountByUuidAndUser(dto.sourceAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + dto.sourceAccountUuid() + "not found!"));

        if (dto.type() == TransactionType.TRANSFER) {
            throw new InvalidTransactionException("Use /transfer endpoint for transfers");
        }

        if (dto.categoryUuid() == null) {
            throw new ValidationException("Category is required for non-transfer transactions");
        }

        Category category = categoryRepository.findCategoryByUuidAndUser(dto.categoryUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Category with uuid: " + dto.categoryUuid() + " not found!"));

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
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));

        Account sourceAccount = accountRepository.findAccountByUuidAndUser(dto.sourceAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + dto.sourceAccountUuid() + "not found!"));

        Account targetAccount = accountRepository.findAccountByUuidAndUser(dto.targetAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + dto.targetAccountUuid() + "not found!"));

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
    public TransactionReadOnlyDTO getTransaction(UUID transUuid, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));
        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with uuid: " + transUuid + " not found!"));

        //RETURN
        return transactionMapper.toReadOnly(transaction);
    }

    @Transactional
    @Override
    public TransactionReadOnlyDTO updateTransaction(UUID transUuid, TransactionUpdateDTO dto, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));
        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with uuid: " + transUuid + " not found!"));
        Category category = dto.categoryId() != null
                ? categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with uuid: " + dto.categoryId() + " not found!"))
                : null;

        //PREPARE
        transaction.setDescription(dto.description());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setCategory(category);

        //EXECUTE
        Transaction updatedTransaction = transactionRepository.save(transaction);

        //RETURN
        return transactionMapper.toReadOnly(updatedTransaction);
    }

    @Override
    public List<TransactionReadOnlyDTO> getAllTransactions(UUID userUuid, Pageable pageable) {
        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));

        //RETURN
        return transactionRepository.findTransByUser(user, pageable)
                .getContent()
                .stream()
                .map(transactionMapper::toReadOnly)
                .toList();
    }

    @Override
    public List<TransactionReadOnlyDTO> getTransactionByAccount(UUID accountUuid, UUID userUuid, Pageable pageable) {
        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + accountUuid + " not found!"));

        //RETURN
        return transactionRepository.findTransByUserAndSourceAccount(user, account, pageable)
                .getContent()
                .stream()
                .map(transactionMapper::toReadOnly)
                .toList();
    }

    @Override
    public List<TransactionReadOnlyDTO> getTransactionByType(TransactionType type, UUID userUuid, Pageable pageable) {
        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));

        //RETURN
        return transactionRepository.findTransByUserAndType(user, type, pageable)
                .getContent()
                .stream()
                .map(transactionMapper::toReadOnly)
                .toList();
    }

    @Transactional
    @Override
    public void deleteTransaction(UUID transUuid, UUID userUuid) throws InvalidTransactionException {
        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + " not found!"));
        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with uuid: " + transUuid + " not found!"));

        //PREPARE
        Account sourceAccount = transaction.getSourceAccount();
        Account targetAccount = transaction.getTargetAccount();

        switch (transaction.getType()) {
            case INCOME -> sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
            case EXPENSE -> sourceAccount.setBalance(sourceAccount.getBalance().add(transaction.getAmount()));
            case TRANSFER -> {
                sourceAccount.setBalance(sourceAccount.getBalance().add(transaction.getAmount()));
                targetAccount.setBalance(targetAccount.getBalance().subtract(transaction.getAmount()));
            }
            default -> throw new InvalidTransactionException("Unknown transaction type");
        }

        //EXECUTE
        transaction.softDelete(Instant.now());
        transactionRepository.save(transaction);
    }
}
