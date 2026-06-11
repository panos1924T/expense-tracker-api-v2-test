package gr.cf9.pants.expense_tracker.controller;

import gr.cf9.pants.expense_tracker.authentication.AuthenticationService;
import gr.cf9.pants.expense_tracker.dto.AuthenticationRequestDTO;
import gr.cf9.pants.expense_tracker.dto.AuthenticationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationService authenticationService;

    public ResponseEntity<AuthenticationResponseDTO> authenticate(AuthenticationRequestDTO dto) {
        AuthenticationResponseDTO responseDTO = authenticationService.authenticate(dto);
        return ResponseEntity.ok(responseDTO);
    }
}
