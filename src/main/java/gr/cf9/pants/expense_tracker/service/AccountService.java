package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.AccountMapper;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
import gr.cf9.pants.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService implements IAccountService{

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid) {

        //VALIDATE


        //PREPARE


        //EXECUTE


        //RETURN
        return null;
    }

    @Override
    public AccountReadOnlyDTO updateAccount(Long id, AccountUpdateDTO dto, UUID userUuid) {

        //VALIDATE


        //PREPARE


        //EXECUTE


        //RETURN
        return null;
    }

    @Override
    public void deleteAccount(Long id, UUID userUuid) {

        //VALIDATE


        //PREPARE


        //EXECUTE


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
