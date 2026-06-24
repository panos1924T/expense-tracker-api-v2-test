package gr.cf9.pants.expense_tracker.core.filters;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TransactionFilters {

    private UUID uuid;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private TransactionType type;
    private UUID categoryUuid;
    private UUID accountUuid;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
}