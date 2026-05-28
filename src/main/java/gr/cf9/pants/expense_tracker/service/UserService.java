package gr.cf9.pants.expense_tracker.service;

import gr.cf9.pants.expense_tracker.core.enums.AccountType;
import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityAlreadyExistsException;
import gr.cf9.pants.expense_tracker.core.exceptions.EntityNotFoundException;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserReadOnlyDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserInsertDTO;
import gr.cf9.pants.expense_tracker.dto.user_dto.UserUpdateDTO;
import gr.cf9.pants.expense_tracker.mapper.UserMapper;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import gr.cf9.pants.expense_tracker.repository.AccountRepository;
import gr.cf9.pants.expense_tracker.repository.CategoryRepository;
import gr.cf9.pants.expense_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) {

        //VALIDATE
        if (userRepository.existsUserByEmail(userInsertDTO.email())) {
            throw new EntityAlreadyExistsException("Email already exists: " + userInsertDTO.email());
        }

        //PREPARE
        String hashedPassword = passwordEncoder.encode(userInsertDTO.password());
        User user = userMapper.toEntity(userInsertDTO, hashedPassword);

        //EXECUTE
        User savedUser = userRepository.save(user);
        createDefaultCategories(savedUser);
        createDefaultCashAccount(savedUser);

        //RETURN

        return userMapper.toReadOnly(savedUser);
    }

    @Transactional
    @Override
    public UserReadOnlyDTO update(UUID userUuid, UserUpdateDTO userUpdateDTO) {

        //VALIDATE
        User user = userRepository.findUserByUuid(userUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found with uuid: " + userUuid));

        if (userRepository.existsUserByEmailAndUuidNot(userUpdateDTO.email(), userUuid)) {
            throw new EntityAlreadyExistsException("Email already exists! " + userUpdateDTO.email());
        }

        //PREPARE
        user.setUsername(userUpdateDTO.username());
        user.setEmail(userUpdateDTO.email());
        user.setPassword(passwordEncoder.encode(userUpdateDTO.password()));

        //EXECUTE
        User updatedUser = userRepository.save(user);

        //RETURN
        return userMapper.toReadOnly(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(UUID uuid) {
        User user = userRepository.findUserByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + uuid + " not found!"));

        user.softDelete(Instant.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserReadOnlyDTO getByUuid(UUID uuid) {
        User user = userRepository.findUserByUuid(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + uuid + " not found!"));
        return userMapper.toReadOnly(user);
    }

    @Override
    public UserReadOnlyDTO getUserByUuidAndDeletedFalse(UUID uuid) {
        User user = userRepository.findUserByUuidAndDeletedFalse(uuid)
                .orElseThrow(() -> new EntityNotFoundException("User with uuid: " + uuid + " not found!"));
        return userMapper.toReadOnly(user);
    }

    private void createDefaultCategories(User user) {

        //Default categories for expenses
        Category personal = new Category();
        personal.setUser(user);
        personal.setName("Personal");
        personal.setType(TransactionType.EXPENSE);

        Category home = new Category();
        home.setUser(user);
        home.setName("Home");
        home.setType(TransactionType.EXPENSE);

        Category buys = new Category();
        buys.setUser(user);
        buys.setName("Buys");
        buys.setType(TransactionType.EXPENSE);

        Category trans = new Category();
        trans.setUser(user);
        trans.setName("Transportation");
        trans.setType(TransactionType.EXPENSE);

        Category other = new Category();
        other.setUser(user);
        other.setName("Other");
        other.setType(TransactionType.EXPENSE);

        Category bills = new Category();
        bills.setUser(user);
        bills.setName("Bills");
        bills.setType(TransactionType.EXPENSE);

        //Default categories for income
        Category salary = new Category();
        salary.setUser(user);
        salary.setName("Salary");
        salary.setType(TransactionType.INCOME);

        Category business = new Category();
        business.setUser(user);
        business.setName("Business");
        business.setType(TransactionType.INCOME);

        Category invest = new Category();
        invest.setUser(user);
        invest.setName("Investment");
        invest.setType(TransactionType.INCOME);

        categoryRepository.saveAll(List.of(personal,home,buys,trans,other,bills,salary,business,invest));
    }

    private void createDefaultCashAccount(User user) {
        Account cashDefault = new Account(
                "Cash",
                BigDecimal.ZERO,
                AccountType.LIQUIDITY,
                true,
                user
        );

        accountRepository.save(cashDefault);
    }
}
