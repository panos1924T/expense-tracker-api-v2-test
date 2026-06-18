package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.*;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.*;
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
    public TransactionReadOnlyDTO createTransaction(TransactionCreateDTO dto, UUID userUuid){
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        if (dto.amount() == null) {
            throw new InvalidArgumentException("Amount", "Amount is required");
        }
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount", "Amount must be positive");
        }

        if (dto.type() == null) {
            throw new InvalidArgumentException("TransactionType", "Transaction type is required");
        }

        if (dto.type() == TransactionType.TRANSFER) {
            throw new InvalidArgumentException("TransactionType", "Use /transfer endpoint for transfers");
        }

        Category category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(dto.categoryUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + dto.categoryUuid() + " not found!"));

        if (category.getParent() == null) {
            throw new InvalidTransactionException("Category", "Parent categories are groups only and cannot be used in transactions");
        }

        if (category.getType() != dto.type()) {
            throw new InvalidTransactionException("Category", "Category type must match transaction type");
        }

        Account sourceAccount;
        BigDecimal newBalance;

        if (dto.type() == TransactionType.INCOME) {
            sourceAccount = accountRepository.findAccountByUserAndDefaultAccountTrue(user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Default account not found for user with uuid=" + userUuid));

            newBalance = sourceAccount.getBalance().add(dto.amount());
            sourceAccount.setBalance(newBalance);

        } else {
            if (dto.sourceAccountUuid() == null) {
                throw new InvalidArgumentException("Account", "Source account is required for EXPENSE transactions");
            }

            sourceAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid=" + dto.sourceAccountUuid() + " not found"));

            newBalance = sourceAccount.getBalance().subtract(dto.amount());
            sourceAccount.setBalance(newBalance);
        }

        //PREPARE
        Transaction transaction = transactionMapper.toEntity(dto, sourceAccount, category);

        //EXECUTE
        transactionRepository.save(transaction);

        //RETURN
        return transactionMapper.toReadOnly(transaction);
    }

    @Transactional
    @Override
    public TransactionReadOnlyDTO createTransfer(TransferCreateDTO dto, UUID userUuid){
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        if (dto.amount() == null) {
            throw new InvalidArgumentException("Amount", "Amount is required");
        }
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount", "Amount must be positive");
        }

        Account sourceAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + dto.sourceAccountUuid() + "not found!"));

        Account targetAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(dto.targetAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + dto.targetAccountUuid() + "not found!"));

        if (sourceAccount.getUuid().equals(targetAccount.getUuid())) {
            throw new InvalidTransactionException("Account", "Source and target account cannot be the same");
        }

        //PREPARE
        Transaction transaction = transactionMapper.toEntity(dto, sourceAccount, targetAccount);
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
    @Transactional(readOnly = true)
    public TransactionReadOnlyDTO getTransactionByUuid(UUID transUuid, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "Transaction with uuid: " + transUuid + " not found!"));

        //RETURN
        return transactionMapper.toReadOnly(transaction);
    }

    @Transactional
    @Override
    public TransactionReadOnlyDTO updateTransaction(UUID transUuid, TransactionUpdateDTO dto, UUID userUuid)
            throws InvalidTransactionException {

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + userUuid + " not found"));

        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "Transaction with uuid: " + transUuid + " not found!"));

        if (dto.amount() == null) {
            throw new InvalidArgumentException("Amount", "Amount is required");
        }
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount", "Amount must be positive");
        }

        if (dto.type() == null) {
            throw new InvalidArgumentException("TransactionType", "Transaction type is required");
        }

        if (transaction.getType() == TransactionType.TRANSFER || dto.type() == TransactionType.TRANSFER) {
            throw new InvalidArgumentException("Transaction", "Use /transfers endpoint for transfer transactions");
        }

        if (dto.categoryUuid() == null) {
            throw new InvalidArgumentException("Category", "Category is required");
        }

        Category newCategory = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(dto.categoryUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid=" + dto.categoryUuid() + " not found"));

        if (newCategory.getParent() == null) {
            throw new InvalidTransactionException("Category", "Parent categories are groups only and cannot be used in transactions");
        }

        if (newCategory.getType() != dto.type()) {
            throw new InvalidTransactionException("Category", "Category type must match transaction type");
        }

        Account newAccount;

        if (transaction.getType() == TransactionType.INCOME) {
            transaction.getSourceAccount().setBalance(
                    transaction.getSourceAccount().getBalance().subtract(transaction.getAmount())
            );
        } else {
            transaction.getSourceAccount().setBalance(
                    transaction.getSourceAccount().getBalance().add(transaction.getAmount())
            );
        }

        if (dto.type() == TransactionType.INCOME) {
            newAccount = accountRepository.findAccountByUserAndDefaultAccountTrue(user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Default account not found"));
            newAccount.setBalance(newAccount.getBalance().add(dto.amount()));
        } else {
            if (dto.sourceAccountUuid() == null) {
                throw new InvalidArgumentException("Account", "Source account is required for EXPENSE");
            }

            newAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid=" + dto.sourceAccountUuid() + " not found"));
            newAccount.setBalance(newAccount.getBalance().subtract(dto.amount()));
        }


        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setSourceAccount(newAccount);
        transaction.setCategory(newCategory);
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setDescription(dto.description());

        return transactionMapper.toReadOnly(transactionRepository.save(transaction));
    }

    @Transactional
    @Override
    public TransactionReadOnlyDTO updateTransfer(UUID transUuid, TransferUpdateDTO dto, UUID userUuid)
            throws InvalidTransactionException {

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + userUuid + " not found"));

        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "Transaction with uuid=" + transUuid + " not found"));

        if (dto.amount() == null) {
            throw new InvalidArgumentException("Amount", "Amount is required");
        }
        if (dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount", "Amount must be positive");
        }

        if (transaction.getType() != TransactionType.TRANSFER) {
            throw new InvalidArgumentException("Transaction", "Transaction with uuid=" + transUuid + " is not a transfer");
        }

        transaction.getSourceAccount()
                .setBalance(transaction.getSourceAccount().getBalance().add(transaction.getAmount()));
        transaction.getTargetAccount()
                .setBalance(transaction.getTargetAccount().getBalance().subtract(transaction.getAmount()));

        Account newSourceAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(dto.sourceAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Source account with uuid=" + dto.sourceAccountUuid() + " not found"));

        Account newTargetAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(dto.targetAccountUuid(), user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Target account with uuid=" + dto.targetAccountUuid() + " not found"));

        if (newSourceAccount.getUuid().equals(newTargetAccount.getUuid())) {
            throw new InvalidTransactionException("Account", "Source and target account cannot be the same");
        }

        newSourceAccount.setBalance(newSourceAccount.getBalance().subtract(dto.amount()));
        newTargetAccount.setBalance(newTargetAccount.getBalance().add(dto.amount()));

        transaction.setAmount(dto.amount());
        transaction.setSourceAccount(newSourceAccount);
        transaction.setTargetAccount(newTargetAccount);
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setDescription(dto.description());

        return transactionMapper.toReadOnly(transactionRepository.save(transaction));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionReadOnlyDTO> getAllTransactions(UUID userUuid, Pageable pageable) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        //RETURN
        return transactionRepository.findTransByUser(user, pageable)
                .getContent()
                .stream()
                .map(transactionMapper::toReadOnly)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionReadOnlyDTO> getTransactionByAccount(UUID accountUuid, UUID userUuid, Pageable pageable) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Account account = accountRepository.findAccountByUuidAndUserAndDeletedFalse(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + accountUuid + " not found!"));

        //RETURN
        return transactionRepository.findTransByUserAndAccount(user, account, pageable)
                .getContent()
                .stream()
                .map(transactionMapper::toReadOnly)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionReadOnlyDTO> getTransactionByCategory(UUID categoryUuid, UUID userUuid, Pageable pageable) {

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        Category category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + " not found!"));

        if (category.getParent() == null) {
            return transactionRepository.findTransByUserAndCategoryParent(user, category, pageable)
                    .getContent()
                    .stream()
                    .map(transactionMapper::toReadOnly)
                    .toList();
        } else {
            return transactionRepository.findTransByUserAndCategory(user, category, pageable)
                    .getContent()
                    .stream()
                    .map(transactionMapper::toReadOnly)
                    .toList();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionReadOnlyDTO> getTransactionByType(TransactionType type, UUID userUuid, Pageable pageable) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        //RETURN
        return transactionRepository.findTransByUserAndType(user, type, pageable)
                .getContent()
                .stream()
                .map(transactionMapper::toReadOnly)
                .toList();
    }

    @Transactional
    @Override
    public void deleteTransaction(UUID transUuid, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "Transaction with uuid: " + transUuid + " not found!"));

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
            default -> throw new InvalidArgumentException("Transaction", "Unknown transaction type");
        }

        //EXECUTE
        transaction.softDelete(Instant.now());
        transactionRepository.save(transaction);
    }
}
