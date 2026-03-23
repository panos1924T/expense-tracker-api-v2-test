package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.exceptions.EntityAlreadyExistsException;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserRegisterDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.UserMapper;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserReadOnlyDTO register(UserRegisterDTO dto) {

        //VALIDATE
        if (userRepository.existsByEmail(dto.email())) {
            throw new EntityAlreadyExistsException("Email already exists: " + dto.email());
        }

        if (userRepository.existsByUsername(dto.username())) {
            throw new EntityAlreadyExistsException("Username already exists: " + dto.username());
        }

        //PREPARE
        String hashedPassword = passwordEncoder.encode(dto.password());
        User user = userMapper.toEntity(dto, hashedPassword);

        //EXECUTE
        User savedUser = userRepository.save(user);

        //RETURN
        return userMapper.toReadOnly(savedUser);
    }

    @Transactional
    @Override
    public UserReadOnlyDTO update(UUID userUuid, UserUpdateDTO dto) {

        //VALIDATE
        User user = userRepository.findByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + userUuid));

        if (userRepository.existsByEmailAndUuidNot(dto.email(), userUuid)) {
            throw new EntityAlreadyExistsException("Email already exists! " + dto.email());
        }

        if (userRepository.existsByUsernameAndUuidNot(dto.username(), userUuid)) {
            throw new EntityAlreadyExistsException("Username already exists! " + dto.username());
        }

        //PREPARE
        user.setUsername(dto.username());
        user.setEmail(dto.email());

        //EXECUTE
        User updatedUser = userRepository.save(user);

        //RETURN
        return userMapper.toReadOnly(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(UUID uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + uuid + " doesn't exist."));

        user.softDelete(Instant.now());
        userRepository.save(user);
    }

    @Override
    public UserReadOnlyDTO getByUuid(UUID uuid) {
        User user = userRepository.findByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + uuid + " doesn't exist."));
        return userMapper.toReadOnly(user);
    }

}
