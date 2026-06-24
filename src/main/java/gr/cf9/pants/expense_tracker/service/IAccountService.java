package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.filters.AccountFilters;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface IAccountService {

    AccountReadOnlyDTO createAccount(AccountCreateDTO dto, UUID userUuid);

    AccountReadOnlyDTO updateAccount(UUID accountUuid, AccountUpdateDTO dto, UUID userUuid);

    void deleteAccount(UUID accountUuid, UUID userUuid);

//    List<AccountReadOnlyDTO> getAllAccounts(UUID userUuid);
//
//    List<AccountReadOnlyDTO> getActiveAccounts(UUID userUuid);
//
//    List<AccountReadOnlyDTO> getAccountsByType(UUID userUuid, AccountType accountType);
//
//    List<AccountReadOnlyDTO> getActiveAccountsByType(UUID userUuid, AccountType accountType);
//
//    AccountReadOnlyDTO getActiveAccountByUuid(UUID accountUuid, UUID userUuid);
//
//    AccountReadOnlyDTO getAccountByUuid(UUID accountUuid, UUID userUuid);

    Page<AccountReadOnlyDTO> getFilteredAndPaginatedAccounts(User user, AccountFilters filters, Pageable pageable);
}
