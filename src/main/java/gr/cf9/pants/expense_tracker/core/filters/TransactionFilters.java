package gr.cf9.pants.expense_tracker.core.filters;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.model.Category;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
@Builder
public class TransactionFilters {
    private Category category;
    private TransactionType type;
    private LocalDate date;

}
