package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.dto.transaction_dto.*;
import gr.cf9.pants.expense_tracker.model.User;
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
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionRestController {

    private final ITransactionService transactionService;

    // 1. ΔΗΜΙΟΥΡΓΙΑ (Έσοδο / Έξοδο / Μεταφορά)
    @PostMapping
    public ResponseEntity<TransactionReadOnlyDTO> createTransaction(
            @Valid @RequestBody TransactionCreateDTO dto,
            @AuthenticationPrincipal User principal) {

        TransactionReadOnlyDTO created = transactionService.createTransaction(dto, principal.getUuid());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(created.uuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // 2. ΟΛΙΚΗ ΕΝΗΜΕΡΩΣΗ (Έσοδο / Έξοδο / Μεταφορά - Χωρίς αλλαγή τύπου)
    @PutMapping("/{uuid}")
    public ResponseEntity<TransactionReadOnlyDTO> updateTransaction(
            @PathVariable UUID uuid,
            @Valid @RequestBody TransactionUpdateDTO dto,
            @AuthenticationPrincipal User principal) {

        return ResponseEntity.ok(transactionService.updateTransaction(uuid, dto, principal.getUuid()));
    }

    // 3. ΔΙΑΓΡΑΦΗ (Κάνει αυτόματο rollback στα accounts)
    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal User principal) {

        transactionService.deleteTransaction(uuid, principal.getUuid());
        return ResponseEntity.noContent().build();
    }

    // 4. ΜΕΜΟΝΩΜΕΝΗ ΑΝΑΓΝΩΣΗ
    @GetMapping("/{uuid}")
    public ResponseEntity<TransactionReadOnlyDTO> getTransaction(
            @PathVariable UUID uuid,
            @AuthenticationPrincipal User principal) {

        return ResponseEntity.ok(transactionService.getTransactionByUuid(uuid, principal.getUuid()));
    }

    // 5. ΑΝΑΖΗΤΗΣΗ & ΦΙΛΤΡΑ (π.χ. ?type=TRANSFER ή ?type=EXPENSE)
    @GetMapping
    public ResponseEntity<List<TransactionReadOnlyDTO>> getTransactions(
            @RequestParam(required = false) TransactionType type,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal User principal) {

        List<TransactionReadOnlyDTO> transactions = (type != null) ?
                transactionService.getTransactionByType(type, principal.getUuid(), pageable) :
                transactionService.getAllTransactions(principal.getUuid(), pageable);
        return ResponseEntity.ok(transactions);
    }
}