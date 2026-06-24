package gr.cf9.pants.expense_tracker.core.filters;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CategoryFilters {

    private String name;
    private TransactionType type;
    private Boolean isParent;
    private UUID parentUuid;
    private boolean includeDeleted;
}