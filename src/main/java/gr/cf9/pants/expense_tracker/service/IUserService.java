package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserRegisterDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;

import java.util.UUID;

public interface IUserService {

    UserReadOnlyDTO register(UserRegisterDTO dto);

    UserReadOnlyDTO update(UUID userUuid, UserUpdateDTO dto);

    void deleteUser(UUID uuid);

    UserReadOnlyDTO getByUuid(UUID uuid);
}
