package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.core.exceptions.UnauthorizedException;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.AccountMapper;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
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

    @Transactional
    @Override
    public AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "does not exist!"));

        //PREPARE
        Account account = accountMapper.toEntity(dto, user);

        //EXECUTE
        Account savedAccount = accountRepository.save(account);

        //RETURN
        return accountMapper.toReadOnly(savedAccount);
    }

    @Transactional
    @Override
    public AccountReadOnlyDTO updateAccount(Long id, AccountUpdateDTO dto, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "does not exist!"));
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to account with id " + id));

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
    public void deleteAccount(Long id, UUID userUuid) {

        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + userUuid + "does not exist!"));
        Account account = accountRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized access to account with id " + id));

        //PREPARE


        //EXECUTE
        account.softDelete(Instant.now());
        accountRepository.save(account);

        //RETURN

    }

    @Override
    public List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid) {

        //VALIDATE


        //PREPARE


        //EXECUTE


        //RETURN
        return List.of();
    }

    @Override
    public AccountReadOnlyDTO getAccount(Long id, UUID userUuid) {

        //VALIDATE


        //PREPARE


        //EXECUTE


        //RETURN
        return null;
    }
}
