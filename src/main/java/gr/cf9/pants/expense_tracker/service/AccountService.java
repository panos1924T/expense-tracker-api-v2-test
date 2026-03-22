package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;

import java.util.List;
import java.util.UUID;

public class AccountService implements IAccountService{

    @Override
    public AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid) {
        return null;
    }

    @Override
    public AccountReadOnlyDTO updateAccount(Long id, AccountUpdateDTO dto, UUID userUuid) {
        return null;
    }

    @Override
    public void deleteAccount(Long id, UUID userUuid) {

    }

    @Override
    public List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid) {
        return List.of();
    }

    @Override
    public AccountReadOnlyDTO getAccount(Long id, UUID userUuid) {
        return null;
    }
}
