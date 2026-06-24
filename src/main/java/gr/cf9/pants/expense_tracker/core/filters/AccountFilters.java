package gr.cf9.pants.expense_tracker.core.filters;

import gr.cf9.pants.expense_tracker.core.enums.AccountType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class AccountFilters {

    private String name;
    private AccountType accountType;
    private Boolean defaultAccount;
    private boolean includeDeleted;
}