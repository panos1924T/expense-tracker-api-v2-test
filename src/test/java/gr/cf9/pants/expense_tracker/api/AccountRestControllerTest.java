package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.enums.AccountType;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountCreateDTO;
import gr.cf9.pants.expense_tracker.dto.account_dto.AccountReadOnlyDTO;
import gr.cf9.pants.expense_tracker.service.IAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Account REST Controller Tests")
class AccountRestControllerTest {

    @Mock
    private IAccountService accountService;

    @InjectMocks
    private AccountRestController controller;

    private UUID testAccountUuid;
    private UUID testUserUuid;
    private AccountCreateDTO createDTO;
    private AccountReadOnlyDTO readOnlyDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUserUuid = UUID.randomUUID();
        testAccountUuid = UUID.randomUUID();

        createDTO = new AccountCreateDTO(
                "Test Account",
                AccountType.SAVINGS,
                new BigDecimal("1000.00")
        );

        readOnlyDTO = new AccountReadOnlyDTO(
                testAccountUuid,
                "Test Account",
                "SAVINGS",
                new BigDecimal("1000.00"),
                Instant.now()
        );
    }

    @Test
    @DisplayName("✅ Service is properly injected")
    void testServiceInjection() {
        assertNotNull(accountService);
        assertNotNull(controller);
    }

    @Test
    @DisplayName("✅ Should have accountService available")
    void testAccountServiceAvailable() {
        when(accountService.createAccount(any(AccountCreateDTO.class), eq(testUserUuid)))
                .thenReturn(readOnlyDTO);

        AccountReadOnlyDTO result = accountService.createAccount(createDTO, testUserUuid);

        assertNotNull(result);
        assertEquals("Test Account", result.name());
    }
}