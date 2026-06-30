package gr.cf9.pants.expense_tracker.authentication;

import gr.cf9.pants.expense_tracker.dto.AuthenticationRequestDTO;
import gr.cf9.pants.expense_tracker.dto.AuthenticationResponseDTO;
import gr.cf9.pants.expense_tracker.model.Role;
import gr.cf9.pants.expense_tracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@DisplayName("Authentication Service Tests")
class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationService authenticationService;

    private AuthenticationRequestDTO authRequest;
    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setPassword("password123");
        testUser.setRole(userRole);

        authRequest = new AuthenticationRequestDTO("user@example.com", "password123");
    }

    @Test
    @DisplayName("Should authenticate user successfully and return token")
    void testAuthenticate_Success() {
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(authentication.getPrincipal()).thenReturn(testUser);
        when(authentication.getName()).thenReturn("user@example.com");

        when(jwtService.generateToken("user@example.com", "ROLE_USER"))
                .thenReturn(expectedToken);

        AuthenticationResponseDTO response = authenticationService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals(expectedToken, response.token());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, times(1)).generateToken("user@example.com", "ROLE_USER");
    }

    @Test
    @DisplayName("Should throw exception for invalid credentials")
    void testAuthenticate_InvalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Invalid credentials"));

        assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class,
                () -> authenticationService.authenticate(authRequest)
        );
    }
}
