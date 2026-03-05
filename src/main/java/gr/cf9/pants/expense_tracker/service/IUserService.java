package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserRegisterDTO;

import java.util.UUID;

public interface IUserService {

    UserReadOnlyDTO register(UserRegisterDTO dto);

    UserReadOnlyDTO getByUuid(UUID uuid);

    void deleteUser(UUID uuid);
}
