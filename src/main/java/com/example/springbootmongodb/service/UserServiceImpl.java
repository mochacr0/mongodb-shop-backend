package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserServiceImpl extends DataBaseService<User, UserEntity> implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommonValidator commonValidator;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailService mailService;
    @Autowired
    private SecuritySettingsConfiguration securitySettings;
    @Autowired
    private DataValidator<User> userDataValidator;
    @Autowired
    private UserService userDeletionService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Override
    public PageData<User> findUsers(PageParameter pageParameter) {
        log.info("Performing UserService findUsers");
        commonValidator.validatePageParameter(pageParameter);
        return DaoUtils.toPageData(userRepository.findAll(DaoUtils.toPageable(pageParameter)));
    }

    @Override
    public MongoRepository<UserEntity, String> getRepository() {
        return this.userRepository;
    }

    @Override
    public User save(User user) {
        log.info("Performing UserService save");
        userDataValidator.validateOnUpdate(user);
        findById(user.getId());
        return super.save(user);
    }

    @Override
    public User findByName(String name) {
        log.info("Performing UserService findByName");
        if (StringUtils.isBlank(name)) {
            throw new InvalidDataException("Username should be specified");
        }
        Optional<UserEntity> userEntityOptional = userRepository.findByName(name);
        if (userEntityOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with name [%s] is not found", name));
        }
        return DaoUtils.toData(userEntityOptional);
    }

    @Override
    public User findById(String userId) {
        log.info("Performing UserService findById");
        if (StringUtils.isBlank(userId)) {
            throw new InvalidDataException("User ID should be specified");
        }
        Optional<UserEntity> userEntityOptional = userRepository.findById(userId);
        if (userEntityOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with id [%s] is not found", userId));
        }
        return DaoUtils.toData(userEntityOptional);
    }

    @Override
    public User findByEmail(String email) {
        log.info("Performing UserService findByEmail");
        if (StringUtils.isBlank(email)) {
            throw new InvalidDataException("User email should be specified");
        }
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        if (userEntityOptional.isEmpty()) {
            throw new ItemNotFoundException(String.format("User with email [%s] is not found", email));
        }
        return DaoUtils.toData(userEntityOptional);
    }

    @Override
    public User register(RegisterUserRequest registerRequest, HttpServletRequest request, boolean isMailRequired) {
        log.info("Performing UserService registerUser");
        commonValidator.validatePasswords(registerRequest.getPassword(), registerRequest.getConfirmPassword());
        User user = new User(registerRequest);
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
        User savedUser = super.insert(user);
        if (StringUtils.isNotBlank(savedUser.getId())) {
            UserCredentials savedUserCredentials = savedUser.getUserCredentials();
            if (StringUtils.isNotBlank(savedUser.getUserCredentials().getActivationToken())) {
                String activateLink = String.format(this.ACTIVATION_URL_PATTERN, UrlUtils.getBaseUrl(request), savedUserCredentials.getActivationToken());
                mailService.sendActivationMail(user.getEmail(), activateLink);
            }
        }
        return savedUser;
    }

    @Override
    @Transactional
    public void deleteById(String userId) {
        log.info("Performing UserService deleteById");
        this.findById(userId);
        this.userRepository.deleteById(userId);
    }

    @Override
    public void deleteUnverifiedUsers() {
        log.info("Performing UserService deleteUnverifiedUsers");
        List<User> unverifiedUsers = DaoUtils.toListData(userRepository.findUnverifiedUsers());
        List<List<User>> chunks = new ArrayList<>();
        int chunkSize = 2;
        for (int currentIndex = 0; currentIndex < unverifiedUsers.size(); currentIndex += chunkSize) {
            chunks.add(unverifiedUsers.subList(currentIndex, Math.min(currentIndex + chunkSize, unverifiedUsers.size())));
        }
        for (List<User> chunk : chunks) {
            taskExecutor.submit(() -> {
                for (User user : chunk) {
                    userDeletionService.deleteById(user.getId());
                }
            });
        }
    }

    @Override
    public void activateById(String id) {
        log.info("Performing userService activateByName");
        User user = this.findById(id);
        user.getUserCredentials().setVerified(true);
        this.save(user);
    }

    @Override
    public User findByActivationToken(String activationToken) {
        log.info("Performing UserService findByActivationToken");
        UserEntity userEntity = userRepository.findByActivationToken(activationToken);
        if (userEntity == null) {
            throw new ItemNotFoundException(String.format("User credentials with activation token [%s] is not found", activationToken));
        }
        return DaoUtils.toData(userEntity);
    }

    @Override
    public User findByPasswordResetToken(String passwordResetToken) {
        log.info("Performing UserService findByPasswordResetToken");
        UserEntity userEntity = userRepository.findByPasswordResetToken(passwordResetToken);
        if (userEntity == null) {
            throw new ItemNotFoundException(String.format("User credentials with password reset token [%s] is not found", passwordResetToken));
        }
        return DaoUtils.toData(userEntity);
    }

    @Override
    public User findCurrentUser() {
        log.info("Performing UserService findCurrentUser");
        SecurityUser securityUser = getCurrentUser();
        return this.findById(securityUser.getId());
    }
}
