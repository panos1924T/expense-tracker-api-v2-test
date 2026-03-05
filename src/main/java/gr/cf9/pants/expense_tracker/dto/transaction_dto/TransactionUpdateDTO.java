package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import gr.cf9.pants.expense_tracker.model.Category;

import java.time.LocalDate;

public record TransactionUpdateDTO(
        String description,
        LocalDate transactionDate,
        Long categoryId
) {}
