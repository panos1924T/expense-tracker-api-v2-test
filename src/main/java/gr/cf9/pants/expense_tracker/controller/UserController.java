package gr.cf9.pants.expense_tracker.controller;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserRegisterDTO;
import gr.cf9.pants.expense_tracker.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserReadOnlyDTO> register(
            @Valid @RequestBody UserRegisterDTO dto) {

        UserReadOnlyDTO responseDTO = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

}
