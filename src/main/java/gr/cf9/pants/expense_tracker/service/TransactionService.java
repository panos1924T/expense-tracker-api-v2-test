package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.*;
import gr.cf9.pants.expense_tracker.core.filters.TransactionFilters;
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
import gr.cf9.pants.expense_tracker.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));

        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount", "Amount must be a positive number");
        }
        if (dto.type() == null) {
            throw new InvalidArgumentException("TransactionType", "Transaction type is required");
        }

        Transaction transaction = transactionMapper.toEntity(dto, user);

        switch (dto.type()) {
            case INCOME -> processIncome(transaction, dto.categoryUuid(), dto.amount(), user);
            case EXPENSE -> processExpense(transaction, dto.categoryUuid(), dto.sourceAccountUuid(), dto.amount(), user);
            case TRANSFER -> processTransfer(transaction, dto.sourceAccountUuid(), dto.targetAccountUuid(), dto.amount(), user);
        }

        return transactionMapper.toReadOnly(transactionRepository.save(transaction));
    }

    @Transactional
    @Override
    public TransactionReadOnlyDTO updateTransaction(UUID transUuid, TransactionUpdateDTO dto, UUID userUuid) {

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid=" + userUuid + " not found"));

        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "Transaction with uuid: " + transUuid + " not found!"));

        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransactionException("Amount", "Amount must be a positive number");
        }

        revertTransactionEffect(transaction);

        transactionMapper.updateEntity(transaction, dto);

        switch (transaction.getType()) {
            case INCOME -> processIncome(transaction, dto.categoryUuid(), dto.amount(), user);
            case EXPENSE -> processExpense(transaction, dto.categoryUuid(), dto.sourceAccountUuid(), dto.amount(), user);
            case TRANSFER -> processTransfer(transaction, dto.sourceAccountUuid(), dto.targetAccountUuid(), dto.amount(), user);
        }

        return transactionMapper.toReadOnly(transactionRepository.save(transaction));
    }

//    @Override
//    @Transactional(readOnly = true)
//    public List<TransactionReadOnlyDTO> getAllTransactions(UUID userUuid, Pageable pageable) {
//        //VALIDATE
//        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
//                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
//
//        //RETURN
//        return transactionRepository.findTransByUser(user, pageable)
//                .getContent()
//                .stream()
//                .map(transactionMapper::toReadOnly)
//                .toList();
//    }
//
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

//    @Override
//    @Transactional(readOnly = true)
//    public List<TransactionReadOnlyDTO> getTransactionByActiveAccount(UUID accountUuid, UUID userUuid, Pageable pageable) {
//        //VALIDATE
//        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
//                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
//        Account account = accountRepository.findAccountByUuidAndUserAndDeletedFalse(accountUuid, user)
//                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + accountUuid + " not found!"));
//
//        //RETURN
//        return transactionRepository.findTransByUserAndAccount(user, account, pageable)
//                .getContent()
//                .stream()
//                .map(transactionMapper::toReadOnly)
//                .toList();
//    }
//
//    @Transactional(readOnly = true)
//    @Override
//    public List<TransactionReadOnlyDTO> getTransactionByAccount(UUID accountUuid, UUID userUuid, Pageable pageable) {
//        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
//                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
//        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
//                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + accountUuid + " not found!"));
//
//        return transactionRepository.findTransByUserAndAccount(user, account, pageable)
//                .getContent()
//                .stream()
//                .map(transactionMapper::toReadOnly)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TransactionReadOnlyDTO> getTransactionByActiveCategory(UUID categoryUuid, UUID userUuid, Pageable pageable) {
//
//        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
//                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
//
//        Category category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user)
//                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + " not found!"));
//
//        return transactionRepository.findTransByUserAndCategoryOrChildCategory(user, category, pageable)
//                .getContent()
//                .stream()
//                .map(transactionMapper::toReadOnly)
//                .toList();
//    }
//
//    @Override
//    public List<TransactionReadOnlyDTO> getTransactionByCategory(UUID categoryUuid, UUID userUuid, Pageable pageable) {
//        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
//                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
//
//        Category category = categoryRepository.findCategoryByUuidAndUser(categoryUuid, user)
//                .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid: " + categoryUuid + " not found!"));
//
//        return transactionRepository.findTransByUserAndCategoryOrChildCategory(user, category, pageable)
//                .getContent()
//                .stream()
//                .map(transactionMapper::toReadOnly)
//                .toList();
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<TransactionReadOnlyDTO> getTransactionByType(TransactionType type, UUID userUuid, Pageable pageable) {
//        //VALIDATE
//        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
//                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
//
//        //RETURN
//        return transactionRepository.findTransByUserAndType(user, type, pageable)
//                .getContent()
//                .stream()
//                .map(transactionMapper::toReadOnly)
//                .toList();
//    }

    @Transactional
    @Override
    public void deleteTransaction(UUID transUuid, UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + " not found!"));
        Transaction transaction = transactionRepository.findTransByUuidAndUser(transUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Transaction", "Transaction with uuid: " + transUuid + " not found!"));
        if (transaction.isDeleted()) throw new InvalidArgumentException("Transaction", "Transaction is already deleted");

        //PREPARE
        revertTransactionEffect(transaction);

        //EXECUTE
        transaction.softDelete(Instant.now());
        transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionReadOnlyDTO> getFilteredTransactions(User user, TransactionFilters filters, Pageable pageable) {

        var specification = TransactionSpecification.build(filters, user);

        return transactionRepository.findAll(specification, pageable)
                .map(transactionMapper::toReadOnly);
    }

    private void processIncome(Transaction transaction, UUID categoryUuid, BigDecimal amount, User user) {
        if (categoryUuid == null) throw new InvalidArgumentException("Category", "Category is required for income transactions");

        Category category;
        if (transaction.getCategory() != null && transaction.getCategory().getUuid().equals(categoryUuid)) {
            category = transaction.getCategory();
        } else {
            category = categoryRepository.findCategoryByUuidAndUserAndTypeAndDeletedFalse(categoryUuid, user, transaction.getType())
                    .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid=" + categoryUuid + " not found"));
            if (category.getType() != TransactionType.INCOME) throw new InvalidTransactionException("Category", "Category must be type INCOME");
        }

        Account defaultAccount;
        if (transaction.getSourceAccount() != null) {
            defaultAccount = transaction.getSourceAccount();
        } else {
            defaultAccount = accountRepository.findAccountByUserAndDefaultAccountTrue(user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Default account not found"));
        }

        defaultAccount.setBalance(defaultAccount.getBalance().add(amount));

        transaction.setCategory(category);
        transaction.setSourceAccount(defaultAccount);
        transaction.setTargetAccount(null);
    }

    private void processExpense(Transaction transaction, UUID categoryUuid, UUID sourceAccountUuid, BigDecimal amount, User user) {

        if (categoryUuid == null) throw new InvalidArgumentException("Category", "Category is required for expense transactions");
        Category category;
        if (transaction.getCategory() != null && transaction.getCategory().getUuid().equals(categoryUuid)) {
            category = transaction.getCategory();
        } else {
            category = categoryRepository.findCategoryByUuidAndUserAndDeletedFalse(categoryUuid, user)
                    .orElseThrow(() -> new EntityNotFoundException("Category", "Category with uuid=" + categoryUuid + " not found"));
            if (category.getType() != TransactionType.EXPENSE) {
                throw new InvalidTransactionException("Category", "Category must be type EXPENSE");
            }
        }

        if (sourceAccountUuid == null) throw new InvalidArgumentException("Account", "Source account is required for expense transactions");
        Account sourceAccount;
        if (transaction.getSourceAccount() != null && transaction.getSourceAccount().getUuid().equals(sourceAccountUuid)) {
            sourceAccount = transaction.getSourceAccount();
        } else {
            sourceAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(sourceAccountUuid, user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid=" + sourceAccountUuid + " not found"));
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));

        transaction.setCategory(category);
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(null);
    }

    private void processTransfer(Transaction transaction, UUID sourceAccountUuid, UUID targetAccountUuid, BigDecimal amount, User user) {
        if (sourceAccountUuid == null || targetAccountUuid == null) throw new InvalidArgumentException("Account", "Both source and target accounts are required for transfers");

        if (sourceAccountUuid.equals(targetAccountUuid)) throw new InvalidTransactionException("Account", "Source and target account cannot be the same");

        Account sourceAccount;
        if (transaction.getSourceAccount() != null && transaction.getSourceAccount().getUuid().equals(sourceAccountUuid)) {
            sourceAccount = transaction.getSourceAccount();
        } else {
            sourceAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(sourceAccountUuid, user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Source account with uuid=" + sourceAccountUuid + " not found"));
        }

        Account targetAccount;
        if (transaction.getTargetAccount() != null && transaction.getTargetAccount().getUuid().equals(targetAccountUuid)) {
            targetAccount = transaction.getTargetAccount();
        } else {
            targetAccount = accountRepository.findAccountByUuidAndUserAndDeletedFalse(targetAccountUuid, user)
                    .orElseThrow(() -> new EntityNotFoundException("Account", "Target account with uuid=" + targetAccountUuid + " not found"));
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
        targetAccount.setBalance(targetAccount.getBalance().add(amount));

        transaction.setCategory(null);
        transaction.setSourceAccount(sourceAccount);
        transaction.setTargetAccount(targetAccount);
    }

    private void revertTransactionEffect(Transaction transaction) {
        Account source = transaction.getSourceAccount();
        Account target = transaction.getTargetAccount();
        BigDecimal amount = transaction.getAmount();

        switch (transaction.getType()) {
            case INCOME -> source.setBalance(source.getBalance().subtract(amount));
            case EXPENSE -> source.setBalance(source.getBalance().add(amount));
            case TRANSFER -> {
                source.setBalance(source.getBalance().add(amount));
                target.setBalance(target.getBalance().subtract(amount));
            }
        }
    }
}
