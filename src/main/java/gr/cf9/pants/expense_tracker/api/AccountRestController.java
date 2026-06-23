package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountUpdateDTO;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.TransactionReadOnlyDTO;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.service.IAccountService;
import gr.cf9.pants.expense_tracker.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountRestController {

    private final IAccountService accountService;
    private final ITransactionService transactionService;

    // 1. ΔΗΜΙΟΥΡΓΙΑ ΛΟΓΑΡΙΑΣΜΟΥ
    @PostMapping
    public ResponseEntity<AccountReadOnlyDTO> createAccount(
            @Valid @RequestBody AccountCreateDTO dto,
            @AuthenticationPrincipal User principal) {

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
            @AuthenticationPrincipal User principal) {

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

    // 4. ΜΕΜΟΝΩΜΕΝΗ ΑΝΑΓΝΩΣΗ
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

    // 5. ΑΝΑΓΝΩΣΗ ΛΙΣΤΑΣ
    @GetMapping
    public ResponseEntity<List<AccountReadOnlyDTO>> getAccounts(
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @AuthenticationPrincipal User principal) {

        List<AccountReadOnlyDTO> accounts = includeDeleted ?
                accountService.getAllAccounts(principal.getUuid()) :
                accountService.getActiveAccounts(principal.getUuid());
        return ResponseEntity.ok(accounts);
    }

    // 6. NESTED GET: Συναλλαγές του συγκεκριμένου λογαριασμού
    @GetMapping("/{accountUuid}/transactions")
    public ResponseEntity<List<TransactionReadOnlyDTO>> getAccountTransactions(
            @PathVariable UUID accountUuid,
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User principal) {

        List<TransactionReadOnlyDTO> transactions = includeDeleted ?
                transactionService.getTransactionByAccount(accountUuid, principal.getUuid(), pageable) :
                transactionService.getTransactionByActiveAccount(accountUuid, principal.getUuid(), pageable);
        return ResponseEntity.ok(transactions);
    }
}