package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.model.User;

import java.util.List;
import java.util.UUID;

public interface IAccountService {

    AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid);

    AccountReadOnlyDTO updateAccount(UUID accountUuid, AccountUpdateDTO dto, UUID userUuid);

    void deleteAccount(UUID accountUuid, UUID userUuid);

    List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid);

    AccountReadOnlyDTO getAccount(UUID accountUuid, UUID userUuid);
}
