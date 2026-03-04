package gr.cf9.pants.expense_tracker.mapper;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserRegisterDTO;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserReadOnlyDTO toReadOnly(User user) {
        return new UserReadOnlyDTO(
                user.getUuid(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    public User toEntity(UserRegisterDTO dto) {
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(dto.password());
        return user;
    }
}
