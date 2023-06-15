package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.model.FailedLoginAttempt;
import com.example.springbootmongodb.model.UserCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserCredentialsServiceImpl implements UserCredentialsService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final SecuritySettingsConfiguration securitySettings;
    public static final String USERNAME_PASSWORD_INCORRECT_MESSAGE = "Authentication failed: The username or password you entered is incorrect. Please try again";
    public static final String ACCOUNT_LOCKED_MESSAGE = "Authentication failed: Username was locked due to security policy";
    public static final String UNVERIFIED_ACCOUNT_MESSAGE = "Authentication failed: Email address has not yet been verified";

    @Override
    public void validatePassword(User user, String password, String clientIpAddress) {
        log.info("Performing UserCredentialsService validatePassword");
        UserCredentials userCredentials = user.getUserCredentials();
        if (!userCredentials.isVerified()) {
            throw new LockedException(UNVERIFIED_ACCOUNT_MESSAGE);
        }
        Map<String, FailedLoginAttempt> failedLoginHistory = userCredentials.getFailedLoginHistory();
        long currentTimeMillis = System.currentTimeMillis();
        boolean isPasswordMatched = passwordEncoder.matches(password, userCredentials.getHashedPassword());
        FailedLoginAttempt currentLoginAttempt = failedLoginHistory.get(clientIpAddress);
        if (currentLoginAttempt == null) {
            if (isPasswordMatched) {
                return;
            }
            //password is not matched, first time login failed
            currentLoginAttempt = new FailedLoginAttempt();
            currentLoginAttempt.setCount(1);
            currentLoginAttempt.setFirstAttemptMillis(currentTimeMillis);
            currentLoginAttempt.setEnabled(true);
            failedLoginHistory.put(clientIpAddress, currentLoginAttempt);
            userService.save(user);
            throw new BadCredentialsException(USERNAME_PASSWORD_INCORRECT_MESSAGE);
        }
        //not the first time
        //password is not matched
        if (!currentLoginAttempt.isEnabled() && currentLoginAttempt.getLockExpirationMillis() > currentTimeMillis) {
            throw new LockedException(ACCOUNT_LOCKED_MESSAGE);
        }
        //password is matched
        if (isPasswordMatched) {
            //remove failed login history record for current client ip address
            failedLoginHistory.remove(clientIpAddress);
            userService.save(user);
            return;
        }
        //not the first time but interval time had passed, so this will be counted as the first time to sign-in fail
        if (currentTimeMillis > currentLoginAttempt.getFirstAttemptMillis() + securitySettings.getFailedLoginIntervalMillis()) {
            currentLoginAttempt.setCount(1);
            currentLoginAttempt.setFirstAttemptMillis(currentTimeMillis);
            currentLoginAttempt.setLockExpirationMillis(0);
            userService.save(user);
            throw new BadCredentialsException(USERNAME_PASSWORD_INCORRECT_MESSAGE);
        }
        int currentFailedLoginCount = currentLoginAttempt.getCount() + 1;
        if (currentFailedLoginCount <= securitySettings.getMaxFailedLoginAttempts()) {
            currentLoginAttempt.setCount(currentFailedLoginCount);
            currentLoginAttempt.setEnabled(true);
            userService.save(user);
            throw new BadCredentialsException(USERNAME_PASSWORD_INCORRECT_MESSAGE);
        }
        currentLoginAttempt.setCount(0);
        currentLoginAttempt.setLockExpirationMillis(currentTimeMillis + securitySettings.getFailedLoginLockExpirationMillis());
        currentLoginAttempt.setEnabled(false);
        userService.save(user);
        throw new LockedException(ACCOUNT_LOCKED_MESSAGE);
    }
}
