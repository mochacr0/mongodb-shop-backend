package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.data.mapper.UserMapper;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.common.utils.UrlUtils;
import com.example.springbootmongodb.common.validator.CommonValidator;
import com.example.springbootmongodb.common.validator.DataValidator;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserCredentials;
import com.example.springbootmongodb.model.UserEntity;
import com.example.springbootmongodb.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends DataBaseService<UserEntity> implements UserService {
    private final UserRepository userRepository;
    private final UserAddressService userAddressService;
    private final CommonValidator commonValidator;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SecuritySettingsConfiguration securitySettings;
    private final DataValidator<UserEntity> userDataValidator;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final UserMapper mapper;
    private final CartService cartService;
    @Autowired
    @Lazy
    private UserService userDeletionService;
    @Override
    public PageData<User> findUsers(PageParameter pageParameter) {
        log.info("Performing UserService findUsers");
        commonValidator.validatePageParameter(pageParameter);
        return DaoUtils.toPageData(userRepository.findAll(DaoUtils.toPageable(pageParameter)), User::fromEntity);
    }

    @Override
    public MongoRepository<UserEntity, String> getRepository() {
        return this.userRepository;
    }

    @Override
    public UserEntity save(UserEntity user) {
        log.info("Performing UserService save");
        Optional<UserEntity> existingUserEntityOpt = userRepository.findById(user.getId());
        if (existingUserEntityOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with id [%s] is not found", user.getId()));
        }
        UserEntity existingUserEntity = existingUserEntityOpt.get();
        existingUserEntity.setName(user.getName());
        existingUserEntity.setDefaultAddressId(user.getDefaultAddressId());
        existingUserEntity.setUserCredentials(user.getUserCredentials());
        userDataValidator.validateOnUpdate(existingUserEntity);
        return super.save(existingUserEntity);
    }

    public UserEntity saveCurrentUser(User user) {
        log.info("Performing UserService saveCurrentUser");
        getCurrentUser();
        return save(mapper.toEntity(user));
    }

    @Override
    public UserEntity findByName(String name) {
        log.info("Performing UserService findByName");
        if (StringUtils.isBlank(name)) {
            throw new InvalidDataException("Username should be specified");
        }
        Optional<UserEntity> userOpt = userRepository.findByName(name);
        if (userOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with name [%s] is not found", name));
        }
        return userOpt.get();
    }

    @Override
    public UserEntity findById(String userId) {
        log.info("Performing UserService findById");
        if (StringUtils.isEmpty(userId)) {
            throw new InvalidDataException("User Id should be specified");
        }
        Optional<UserEntity> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with Id [%s] is not found", userId));
        }
        return userOpt.get();
    }

    @Override
    public UserEntity findByEmail(String email) {
        log.info("Performing UserService findByEmail");
        if (StringUtils.isBlank(email)) {
            throw new InvalidDataException("User email should be specified");
        }
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with email [%s] is not found", email));
        }
        return userOpt.get();
    }

    @Override
    public UserEntity register(RegisterUserRequest registerRequest, HttpServletRequest request, boolean isMailRequired) {
        log.info("Performing UserService registerUser");
        commonValidator.validatePasswords(registerRequest.getPassword(), registerRequest.getConfirmPassword());
        UserEntity user = mapper.toEntity(registerRequest);
        userDataValidator.validateOnCreate(user);
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setHashedPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userCredentials.setVerified(false);
        if (isMailRequired) {
            String activateToken = RandomStringUtils.randomAlphanumeric(this.DEFAULT_TOKEN_LENGTH);
            userCredentials.setActivationToken(activateToken);
            userCredentials.setActivationTokenExpirationMillis(System.currentTimeMillis() + securitySettings.getActivationTokenExpirationMillis());
        }
        user.setUserCredentials(userCredentials);
        UserEntity savedUserEntity = super.insert(user);
        if (StringUtils.isNotEmpty(savedUserEntity.getId())) {
            UserCredentials savedUserCredentials = savedUserEntity.getUserCredentials();
            if (StringUtils.isNotEmpty(savedUserEntity.getUserCredentials().getActivationToken())) {
                String activateLink = String.format(this.ACTIVATION_URL_PATTERN, UrlUtils.getBaseUrl(request), savedUserCredentials.getActivationToken());
                mailService.sendActivationMail(user.getEmail(), activateLink);
            }
        }
        cartService.create(savedUserEntity.getId());
        return savedUserEntity;
    }

    @Override
    @Transactional
    public void deleteById(String userId) {
        log.info("Performing UserService deleteById");
        findById(userId);
        userAddressService.deleteUserAddressesByUserId(userId);
        cartService.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    @Override
    public void deleteUnverifiedUsers() {
        log.info("Performing UserService deleteUnverifiedUsers");
        List<User> unverifiedUsers = DaoUtils.toListData(userRepository.findUnverifiedUsers(), User::fromEntity);
        List<List<User>> chunks = new ArrayList<>();
        int chunkSize = 2;
        for (int currentIndex = 0; currentIndex < unverifiedUsers.size(); currentIndex += chunkSize) {
            chunks.add(unverifiedUsers.subList(currentIndex, Math.min(currentIndex + chunkSize, unverifiedUsers.size())));
        }
        for (List<User> chunk : chunks) {
            taskExecutor.submit(() -> {
                for (User user : chunk) {
                    try {
                        userDeletionService.deleteById(user.getId());
                    }
                    catch (Exception ex) {
                        log.error(ex.getMessage());
                    }
                }
            });
        }
    }

    @Override
    public void activateById(String id) {
        log.info("Performing userService activateByName");
        UserEntity user = this.findById(id);
        user.getUserCredentials().setVerified(true);
        this.save(user);
    }

    @Override
    public UserEntity findByActivationToken(String activationToken) {
        log.info("Performing UserService findByActivationToken");
        UserEntity userEntity = userRepository.findByActivationToken(activationToken);
        if (userEntity == null) {
            throw new ItemNotFoundException(String.format("User credentials with activation token [%s] is not found", activationToken));
        }
        return userEntity;
    }

    @Override
    public UserEntity findByPasswordResetToken(String passwordResetToken) {
        log.info("Performing UserService findByPasswordResetToken");
        UserEntity userEntity = userRepository.findByPasswordResetToken(passwordResetToken);
        if (userEntity == null) {
            throw new ItemNotFoundException(String.format("User credentials with password reset token [%s] is not found", passwordResetToken));
        }
        return userEntity;
    }

    @Override
    public UserEntity findCurrentUser() {
        log.info("Performing UserService findCurrentUser");
        SecurityUser securityUser = getCurrentUser();
        return this.findById(securityUser.getId());
    }
}
