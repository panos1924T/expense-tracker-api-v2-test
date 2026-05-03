package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.exceptions.EntityHasTransactionsException;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.core.exceptions.InvalidArgumentException;
import gr.cf9.pants.expense_tracker.core.exceptions.UnauthorizedException;
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
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "not found!"));

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
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + accountUuid + "not found!"));

        //PREPARE
        account.setName(dto.name());
        account.setAccountType(dto.accountType());

        //EXECUTE
        Account updatedAccount = accountRepository.save(account);

        //RETURN
        return accountMapper.toReadOnly(updatedAccount);
    }

    @Transactional
    @Override
    public void deleteAccount(UUID accountUuid, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(accountUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + accountUuid + "not found!"));

        if (account.isDefault()) {
            throw new InvalidArgumentException("Cannot delete default account!");
        }
        if (transactionRepository.existsTransByAccount(account)) {
            throw new EntityHasTransactionsException("Cannot delete entity with existing transactions");
        }

        //EXECUTE
        account.softDelete(Instant.now());
        accountRepository.save(account);
    }

    @Override
    public List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "not found!"));

        //PREPARE
        List<Account> accounts = accountRepository.findAccountByUser(user);

        //EXECUTE AND RETURN
        return accounts.stream()
                .map(accountMapper::toReadOnly)
                .toList();
    }

    @Override
    public AccountReadOnlyDTO getAccount(UUID accountUuid, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "not found!"));
        Account account = accountRepository.findAccountByUuidAndUser(userUuid, user)
                .orElseThrow(() -> new EntityNotFoundException("Account with uuid: " + accountUuid + "not found!"));

        //RETURN
        return accountMapper.toReadOnly(account);
    }
}
