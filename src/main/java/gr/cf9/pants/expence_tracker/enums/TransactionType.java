package gr.cf9.pants.expence_tracker.enums;

public enum TransactionType {

    INCOME,     // Income (Source -> Account)
    EXPENSE,    // Expense (Account -> Merchant/Category)
    TRANSFER    // Transfer (Account A -> Account B)
}
