package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.exceptions.ValidationException;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserInsertDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;
import gr.cf9.pants.expense_tracker.service.IUserService;
import gr.cf9.pants.expense_tracker.validator.UserInsertValidator;
import gr.cf9.pants.expense_tracker.validator.UserUpdateValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService userService;
    private final UserInsertValidator userInsertValidator;
    private final UserUpdateValidator userUpdateValidator;

    @PostMapping
    public ResponseEntity<UserReadOnlyDTO> saveUser(
            @Valid @RequestBody UserInsertDTO userInsertDTO,
            BindingResult bindingResult) {

        userInsertValidator.validate(userInsertDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("User", "Invalid user data", bindingResult);
        }

        UserReadOnlyDTO userReadOnlyDTO = userService.saveUser(userInsertDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{uuid}")
                .buildAndExpand(userReadOnlyDTO.uuid())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(userReadOnlyDTO);
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> updateUser(
            @PathVariable UUID uuid,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO,
            BindingResult bindingResult) {

        userUpdateValidator.validate(userUpdateDTO, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new ValidationException("User", "Invalid user data", bindingResult);
        }

        UserReadOnlyDTO userReadOnlyDTO = userService.updateUser(uuid, userUpdateDTO);
        return ResponseEntity.ok(userReadOnlyDTO);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> deleteUserByUuid(
            @PathVariable UUID uuid
    ) {
        UserReadOnlyDTO userReadOnlyDTO = userService.deleteUser(uuid);
        return ResponseEntity.ok(userReadOnlyDTO);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> getUserByUuid(
            @PathVariable UUID uuid,
            @RequestParam(defaultValue = "false") boolean includeDeleted
    ) {
        UserReadOnlyDTO userReadOnlyDTO = includeDeleted ?
                userService.getUserByUuid(uuid) :
                userService.getUserByUuidAndDeletedFalse(uuid);

        return ResponseEntity.ok(userReadOnlyDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UserReadOnlyDTO>> getAllUsers(
            @RequestParam(defaultValue = "false") boolean includeDeleted,
            @PageableDefault(size = 10, sort = "email") Pageable pageable
    ) {
        Page<UserReadOnlyDTO> usersPaginated = includeDeleted ?
                userService.getAllUsers(pageable) :
                userService.getAllUsersDeletedFalse(pageable);

        return ResponseEntity.ok(usersPaginated);
    }
}
