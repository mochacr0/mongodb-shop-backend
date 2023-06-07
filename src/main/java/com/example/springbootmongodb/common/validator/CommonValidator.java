package com.example.springbootmongodb.common.validator;

import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.config.SecuritySettingsConfiguration;
import com.example.springbootmongodb.exception.IncorrectParameterException;
import com.example.springbootmongodb.exception.InvalidDataException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;

import java.util.List;

@RequiredArgsConstructor
public class CommonValidator {
    private final SecuritySettingsConfiguration securitySettings;
    public void validatePasswords(String password, String confirmPassword) {
        if (Strings.isEmpty(password)) {
            throw new InvalidDataException("Password field cannot be empty");
        }
        if (Strings.isEmpty(confirmPassword)) {
            throw new InvalidDataException("Confirm password field cannot be empty");
        }
        if (!password.equals(confirmPassword)) {
            throw new InvalidDataException("Password and confirm password are not matched");
        }
        validateRawPassword(password);
    }

    public void validateRawPassword(String rawPassword) {
        List<Rule> passwordRules = securitySettings.getPasswordPolicy().getPasswordRules();
        PasswordValidator validator = new PasswordValidator(passwordRules);
        RuleResult validateResult = validator.validate(new PasswordData(rawPassword));
        if (!validateResult.isValid()) {
            String violationMessage = String.join("\n", validator.getMessages(validateResult));
            throw new InvalidDataException(violationMessage);
        }
        //validate password reuse frequency
    }

    public void validatePageParameter(PageParameter pageParameter) {
        if (pageParameter.getPage() < 0) {
            throw new IncorrectParameterException("Page number should be positive");
        }
        if (pageParameter.getPageSize() < 0) {
            throw new IncorrectParameterException("Page size should be positive");
        }
    }
}
