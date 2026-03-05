package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import java.time.LocalDate;

public record TransactionUpdateDTO(
        String description,
        LocalDate transactionDate,
        Long categoryId
) {}
