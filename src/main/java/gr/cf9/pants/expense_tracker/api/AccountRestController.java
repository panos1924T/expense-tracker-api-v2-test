package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.exceptions.ValidationException;
import gr.cf9.pants.expense_tracker.core.filters.AccountFilters;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.service.IAccountService;
import gr.cf9.pants.expense_tracker.service.ITransactionService;
import gr.cf9.pants.expense_tracker.validator.AccountInsertValidator;
import gr.cf9.pants.expense_tracker.validator.AccountUpdateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final IAccountService accountService;
    private final ITransactionService transactionService;
    private final AccountInsertValidator insertValidator;
    private final AccountUpdateValidator updateValidator;

    // 1. ΔΗΜΙΟΥΡΓΙΑ ΛΟΓΑΡΙΑΣΜΟΥ
    @PostMapping
    public ResponseEntity<AccountReadOnlyDTO> createAccount(
            @Valid @RequestBody AccountCreateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User principal) {

        insertValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Account", "Invalid account data", bindingResult);
        }

        AccountReadOnlyDTO created = accountService.createAccount(dto, principal.getUuid());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(created.uuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // 2. ΕΝΗΜΕΡΩΣΗ ΛΟΓΑΡΙΑΣΜΟΥ
    @PutMapping("/{uuid}")
    public ResponseEntity<AccountReadOnlyDTO> updateAccount(
            @PathVariable UUID uuid,
            @Valid @RequestBody AccountUpdateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User principal) {

        updateValidator.validate(uuid, dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Account", "Invalid account data", bindingResult);
        }

        return ResponseEntity.ok(accountService.updateAccount(uuid, dto, principal.getUuid()));
    }

    // 3. ΔΙΑΓΡΑΦΗ (SOFT DELETE)
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal User principal) {

        accountService.deleteAccount(uuid, principal.getUuid());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AccountReadOnlyDTO>> getFilteredPaginatedAccounts(
            @AuthenticationPrincipal User principal,
            @ModelAttribute AccountFilters filters,
            @PageableDefault(sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<AccountReadOnlyDTO> accountsPage = accountService.getFilteredAndPaginatedAccounts(principal, filters, pageable);
        return ResponseEntity.ok(accountsPage);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<AccountReadOnlyDTO> getAccount(
            @PathVariable UUID uuid,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @AuthenticationPrincipal User principal) {

        AccountReadOnlyDTO account = includeDeleted ?
                accountService.getAccountByUuid(uuid, principal.getUuid()) :
                accountService.getActiveAccountByUuid(uuid, principal.getUuid());
        return ResponseEntity.ok(account);
    }
//
//    // 5. ΑΝΑΓΝΩΣΗ ΛΙΣΤΑΣ
//    @GetMapping
//    public ResponseEntity<List<AccountReadOnlyDTO>> getFilteredPaginatedAccounts(
//            @RequestParam(defaultValue = "false") boolean includeDeleted,
//            @AuthenticationPrincipal User principal) {
//
//        List<AccountReadOnlyDTO> accounts = includeDeleted ?
//                accountService.getAllAccounts(principal.getUuid()) :
//                accountService.getActiveAccounts(principal.getUuid());
//        return ResponseEntity.ok(accounts);
//    }
//
//     6. NESTED GET: Συναλλαγές του συγκεκριμένου λογαριασμού
//    @GetMapping("/{accountUuid}/transactions")
//    public ResponseEntity<List<TransactionReadOnlyDTO>> getAccountTransactions(
//            @PathVariable UUID accountUuid,
//            @RequestParam(defaultValue = "false") boolean includeDeleted,
//            @PageableDefault(size = 20) Pageable pageable,
//            @AuthenticationPrincipal User principal) {
//
//        List<TransactionReadOnlyDTO> transactions = includeDeleted ?
//                transactionService.getTransactionByAccount(accountUuid, principal.getUuid(), pageable) :
//                transactionService.getTransactionByActiveAccount(accountUuid, principal.getUuid(), pageable);
//        return ResponseEntity.ok(transactions);
//    }
}