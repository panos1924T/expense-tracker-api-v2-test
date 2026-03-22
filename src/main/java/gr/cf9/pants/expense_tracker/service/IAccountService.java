package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.model.User;

import java.util.List;
import java.util.UUID;

public interface IAccountService {

    AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid);

    AccountReadOnlyDTO updateAccount(Long id, AccountUpdateDTO dto, UUID userUuid);

    void deleteAccount(Long id, UUID userUuid);

    List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid);

    AccountReadOnlyDTO getAccount(Long id, UUID userUuid);
}
