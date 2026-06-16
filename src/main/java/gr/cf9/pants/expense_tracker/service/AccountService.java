package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.exceptions.EntityHasTransactionsException;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.core.exceptions.InvalidArgumentException;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.AccountMapper;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
import gr.cf9.pants.expense_tracker.repository.TransactionRepository;
import gr.cf9.pants.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService implements IAccountService{

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + "not found!"));

        //PREPARE
        Account account = accountMapper.toEntity(dto, user);

        //EXECUTE
        Account savedAccount = accountRepository.save(account);

        //RETURN
        return accountMapper.toReadOnly(savedAccount);
    }

    @Transactional
    @Override
    public AccountReadOnlyDTO updateAccount(UUID accountUuid, AccountUpdateDTO dto, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + "not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + accountUuid + "not found!"));

        if (account.isDefaultAccount() == true) {
            throw new InvalidArgumentException("Account", "Cannot update default account");
        }

        if (account.isDeleted() == true) {
            throw new InvalidArgumentException("Account", "Account with uuid:" + accountUuid + " is deleted");
        }

        //PREPARE
        account.setName(dto.name());
        account.setAccountType(dto.accountType());      //TODO same problem with category, if acc has LIQUIDITY trans should it be updated to CREDIT?

        //EXECUTE
        Account updatedAccount = accountRepository.save(account);

        //RETURN
        return accountMapper.toReadOnly(updatedAccount);
    }

    @Transactional
    @Override
    public void deleteAccount(UUID accountUuid, UUID userUuid) {

        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + "not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + accountUuid + "not found!"));

        if (account.isDeleted() == true) {
            throw new InvalidArgumentException("Account", "Account with uuid:" + accountUuid + " is already deleted");     //TODO will a deleted account be reversed? If not I should say deleted not archived
        }

        if (account.isDefaultAccount()) {
            throw new InvalidArgumentException("Account", "Cannot delete default account!");
        }

        boolean hasTrans = transactionRepository.existsTransByAccount(account);

        if (hasTrans == true) {
            account.softDelete(Instant.now());
        } else {
            accountRepository.delete(account);
        }

        accountRepository.save(account);
    }

    @Override
    public List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + "not found!"));

        //PREPARE
        List<Account> accounts = accountRepository.findAccountByUser(user);

        //EXECUTE AND RETURN
        return accounts.stream()
                .map(accountMapper::toReadOnly)
                .toList();
    }

    @Override
    public List<AccountReadOnlyDTO> getActiveAccounts(UUID userUuid) {
        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + "not found!"));

        //PREPARE
        List<Account> accounts = accountRepository.findAccountByUserAndDeletedFalse(user);

        //EXECUTE AND RETURN
        return accounts.stream()
                .map(accountMapper::toReadOnly)
                .toList();
    }

    @Override
    public AccountReadOnlyDTO getAccount(UUID accountUuid, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuidAndDeletedFalse(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User", "User with uuid: " + userUuid + "not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account", "Account with uuid: " + accountUuid + "not found!"));

        if (account.isDeleted() == true) {
            throw new InvalidArgumentException("Account", "Account with uuid:" + accountUuid + " is deleted");
        }

        //RETURN
        return accountMapper.toReadOnly(account);
    }
}
