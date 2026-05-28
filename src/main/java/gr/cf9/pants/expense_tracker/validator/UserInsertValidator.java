package gr.cf9.pants.expense_tracker.validator;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserInsertDTO;
import gr.cf9.pants.expense_tracker.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserInsertValidator implements Validator {

    private final IUserService userService;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserInsertDTO.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserInsertDTO userInsertDTO = (UserInsertDTO) target;

        if (userService.isUserExists(userInsertDTO.email())) {
            log.warn("Save failed. User with email={} already exists", userInsertDTO.email());
            errors.rejectValue(
                    "email",
                    "email.user.exists",
                    "Email already exists"
            );
        }
    }
}
