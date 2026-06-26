package gr.cf9.pants.expense_tracker.api;

import gr.cf9.pants.expense_tracker.core.exceptions.ValidationException;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserInsertDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;
import gr.cf9.pants.expense_tracker.service.IUserService;
import gr.cf9.pants.expense_tracker.validator.UserInsertValidator;
import gr.cf9.pants.expense_tracker.validator.UserUpdateValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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


    @Operation(summary="Create a User", description="Creates a new User")
    @ApiResponses({
            @ApiResponse(
                    responseCode="201", description="Created", content=@Content(schema=@Schema(implementation=UserReadOnlyDTO.class))),
            @ApiResponse(
                    responseCode="400", description="Validation error"
            )
    })
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



    @Operation(summary="Update a User", description="Updates an existing User")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK", content=@Content(schema=@Schema(implementation=UserReadOnlyDTO.class))),
            @ApiResponse(responseCode="400", description="Validation error"),
            @ApiResponse(responseCode="404", description="Not found"),
            @ApiResponse(responseCode="403", description="Forbidden")
    })
    @SecurityRequirement(name = "Bearer Authentication")
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



    @Operation(summary="Delete a User", description="Deletes a User (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK", content=@Content(schema=@Schema(implementation=UserReadOnlyDTO.class))),
            @ApiResponse(responseCode="404", description="Not found"),
            @ApiResponse(responseCode="403", description="Forbidden")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<UserReadOnlyDTO> deleteUserByUuid(
            @PathVariable UUID uuid
    ) {
        UserReadOnlyDTO userReadOnlyDTO = userService.deleteUser(uuid);
        return ResponseEntity.ok(userReadOnlyDTO);
    }



    @Operation(summary="Get a User", description="Returns a User by UUID")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK", content=@Content(schema=@Schema(implementation=UserReadOnlyDTO.class))),
            @ApiResponse(responseCode="404", description="Not found"),
            @ApiResponse(responseCode="403", description="Forbidden")
    })
    @SecurityRequirement(name = "Bearer Authentication")
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



    @Operation(summary="List Users", description="Returns paginated Users")
    @ApiResponses({
            @ApiResponse(responseCode="200", description="OK", content=@Content(schema=@Schema(implementation=org.springframework.data.domain.Page.class))),
            @ApiResponse(responseCode="403", description="Forbidden")
    })
    @SecurityRequirement(name = "Bearer Authentication")
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
