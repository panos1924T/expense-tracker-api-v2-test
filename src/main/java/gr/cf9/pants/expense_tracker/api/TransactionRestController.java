package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.ValidationException;
import gr.cf9.pants.expense_tracker.core.filters.TransactionFilters;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.*;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.service.ITransactionService;
import gr.cf9.pants.expense_tracker.validator.TransactionInsertValidator;
import gr.cf9.pants.expense_tracker.validator.TransactionUpdateValidator;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final ITransactionService transactionService;
    private final TransactionInsertValidator insertValidator;
    private final TransactionUpdateValidator updateValidator;

    @PostMapping
    public ResponseEntity<TransactionReadOnlyDTO> createTransaction(
            @Valid @RequestBody TransactionCreateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User principal) {

        insertValidator.validate(dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Transaction", "Invalid transaction data", bindingResult);
        }

        TransactionReadOnlyDTO created = transactionService.createTransaction(dto, principal.getUuid());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(created.uuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<TransactionReadOnlyDTO> updateTransaction(
            @PathVariable UUID uuid,
            @Valid @RequestBody TransactionUpdateDTO dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User principal) {

        updateValidator.validate(uuid, dto, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("Transaction", "Invalid transaction data", bindingResult);
        }

        return ResponseEntity.ok(transactionService.updateTransaction(uuid, dto, principal.getUuid()));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal User principal) {

        transactionService.deleteTransaction(uuid, principal.getUuid());
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{uuid}")
//    public ResponseEntity<TransactionReadOnlyDTO> getTransaction(
//            @PathVariable UUID uuid,
//            @AuthenticationPrincipal User principal) {
//
//        return ResponseEntity.ok(transactionService.getTransactionByUuid(uuid, principal.getUuid()));
//    }
//
//    @GetMapping
//    public ResponseEntity<List<TransactionReadOnlyDTO>> getTransactions(
//            @RequestParam(required = false) TransactionType type,
//            @PageableDefault(size = 20) Pageable pageable,
//            @AuthenticationPrincipal User principal) {
//
//        List<TransactionReadOnlyDTO> transactions = (type != null) ?
//                transactionService.getTransactionByType(type, principal.getUuid(), pageable) :
//                transactionService.getAllTransactions(principal.getUuid(), pageable);
//        return ResponseEntity.ok(transactions);
//    }

    @GetMapping
    public ResponseEntity<Page<TransactionReadOnlyDTO>> getTransactions(
            @AuthenticationPrincipal User principal,
            @ModelAttribute TransactionFilters filters,
            @PageableDefault(sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<TransactionReadOnlyDTO> transactionsPage = transactionService.getFilteredTransactions(principal, filters, pageable);
        return ResponseEntity.ok(transactionsPage);
    }
}