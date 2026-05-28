package gr.cf9.pants.expense_tracker.validator;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;
import gr.cf9.pants.expense_tracker.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserUpdateValidator implements Validator {

    private final IUserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserUpdateDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserUpdateDTO userUpdateDTO = (UserUpdateDTO) target;

        String currentEmail = SecurityContextHolder.getContext()
                                                    .getAuthentication()
                                                    .getName();

        if (!userUpdateDTO.email().equals(currentEmail)  && userService.isUserExists(userUpdateDTO.email())) {
            log.warn("Update failed. User with email={} already exists", userUpdateDTO.email());
            errors.rejectValue(
                    "email",
                    "email.user.exists",
                    "Email already exists"
            );
        }
    }
}
