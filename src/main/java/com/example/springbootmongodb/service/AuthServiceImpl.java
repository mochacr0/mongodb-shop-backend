package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ChangePasswordRequest;
import com.example.springbootmongodb.common.data.PasswordResetRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.common.utils.UrlUtils;
import com.example.springbootmongodb.common.validator.CommonValidator;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.exception.IncorrectParameterException;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.exception.ItemNotFoundException;
import com.example.springbootmongodb.model.UserCredentials;
import com.example.springbootmongodb.security.JwtToken;
import com.example.springbootmongodb.security.JwtTokenFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl extends AbstractService implements AuthService {
    private final UserService userService;
    private final SecuritySettingsConfiguration securitySettings;
    private final MailService mailService;
    private final CommonValidator commonValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenFactory jwtTokenFactory;
    @Override
    public void activateEmail(String activationToken) {
        log.info("Performing service activateEmail");
        if (StringUtils.isEmpty(activationToken)) {
            throw new IncorrectParameterException("Activation token cannot be empty");
        }
        User user = userService.findByActivationToken(activationToken);
        if (user == null) {
            throw new ItemNotFoundException(String.format("Unable to find user with given activation token [%s]", activationToken));
        }
        UserCredentials userCredentials = user.getUserCredentials();
        //expired activation token
        if (userCredentials.getActivationTokenExpirationMillis() <= System.currentTimeMillis()) {
            userCredentials.setActivationToken(null);
            userCredentials.setActivationTokenExpirationMillis(0);
            userService.save(user);
            throw new IncorrectParameterException(String.format("Activation token is no longer valid: [%s]", activationToken));
        }
        //valid activation token
        userCredentials.setVerified(true);
        userCredentials.setActivationToken(null);
        userCredentials.setActivationTokenExpirationMillis(0);
        userService.save(user);
    }

    @Override
    public void resendActivationTokenByEmail(String email, HttpServletRequest request) {
        log.info("Performing service resendActivationTokenByEmail");
        if (StringUtils.isEmpty(email)) {
            throw new IncorrectParameterException("Email cannot be empty");
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ItemNotFoundException(String.format("Unable to find user with given email [%s]", email));
        }
        UserCredentials userCredentials = user.getUserCredentials();
        if (userCredentials.isVerified()) {
            throw new IncorrectParameterException(String.format("User with given email [%s] is already verified", user.getEmail()));
        }
        String activationToken = RandomStringUtils.randomAlphanumeric(this.DEFAULT_TOKEN_LENGTH);
        userCredentials.setActivationToken(activationToken);
        userCredentials.setActivationTokenExpirationMillis(System.currentTimeMillis() + securitySettings.getActivationTokenExpirationMillis());
        userService.save(user);
        String activationLink = String.format(this.ACTIVATION_URL_PATTERN, UrlUtils.getBaseUrl(request), activationToken);
        mailService.sendActivationMail(email, activationLink);
    }

    @Override
    public JwtToken changePassword(ChangePasswordRequest request) {
        log.info("Performing service changePassword");
        if (StringUtils.isEmpty(request.getCurrentPassword())) {
            throw new InvalidDataException("Current password cannot be empty");
        }
        commonValidator.validatePasswords(request.getNewPassword(), request.getConfirmPassword());
        SecurityUser currentUser = this.getCurrentUser();
        User user = userService.findById(currentUser.getId());
        if (user == null) {
            throw new ItemNotFoundException("Unable to find current user");
        }
        UserCredentials userCredentials = user.getUserCredentials();
        if (!passwordEncoder.matches(request.getCurrentPassword(), userCredentials.getHashedPassword())) {
            throw new InvalidDataException("Current password is not matched");
        }
        if (!securitySettings.getPasswordPolicy().isRepeatedPasswordAllowed() &&
                request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new InvalidDataException("New password must be different from the current password");
        }
        userCredentials.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);
        return jwtTokenFactory.createAccessToken(currentUser);
    }

    @Override
    public void requestPasswordResetEmail(String email, HttpServletRequest request) {
        log.info("Performing requestPasswordResetEmail service");
        if (StringUtils.isBlank(email)) {
            throw new InvalidDataException("Email cannot be empty");
        }
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ItemNotFoundException(String.format("Unable to find user with given email [%s]", email));
        }
        UserCredentials userCredentials = user.getUserCredentials();
        if (!userCredentials.isVerified()) {
            throw new DisabledException("User account is not verified");
        }
        String passwordResetToken = RandomStringUtils.randomAlphanumeric(this.DEFAULT_TOKEN_LENGTH);
        userCredentials.setPasswordResetToken(passwordResetToken);
        userCredentials.setPasswordResetTokenExpirationMillis(System.currentTimeMillis() + securitySettings.getPasswordResetTokenExpirationMillis());
        userService.save(user);
        String passwordResetLink = String.format(this.PASSWORD_RESET_PATTERN, UrlUtils.getBaseUrl(request), passwordResetToken);
        mailService.sendPasswordResetMail(email, passwordResetLink);
    }

    @Override
    public void resetPassword(PasswordResetRequest request) {
        log.info("Performing resetPassword service");
        if (StringUtils.isBlank(request.getPasswordResetToken())) {
            throw new InvalidDataException("Password reset token cannot be empty");
        }
        commonValidator.validatePasswords(request.getNewPassword(), request.getConfirmPassword());
        User user = userService.findByPasswordResetToken(request.getPasswordResetToken());
        UserCredentials userCredentials = user.getUserCredentials();
        if (userCredentials.getPasswordResetTokenExpirationMillis() < System.currentTimeMillis()) {
            throw new InvalidDataException("Invalid password reset token");
        }
        if (passwordEncoder.matches(request.getNewPassword(), userCredentials.getHashedPassword())) {
            throw new InvalidDataException("New password must be different from the current password");
        }
        userCredentials.setPasswordResetToken(null);
        userCredentials.setPasswordResetTokenExpirationMillis(0);
        userCredentials.setHashedPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.save(user);
    }
}
