package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserInsertDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface IUserService {

    UserReadOnlyDTO saveUser(UserInsertDTO dto);

    UserReadOnlyDTO updateUser(UUID userUuid, UserUpdateDTO dto);

    UserReadOnlyDTO deleteUser(UUID uuid);

    Page<UserReadOnlyDTO> getAllUsers(Pageable pageable);

    Page<UserReadOnlyDTO> getAllUsersDeletedFalse(Pageable pageable);

    UserReadOnlyDTO getUserByUuid(UUID uuid);

    UserReadOnlyDTO getUserByUuidAndDeletedFalse(UUID uuid);

    boolean isUserExists(String email);
}
