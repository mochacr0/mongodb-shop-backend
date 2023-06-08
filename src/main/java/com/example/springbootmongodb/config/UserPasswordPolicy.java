package com.example.springbootmongodb.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.passay.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"$$beanFactory"})
public class UserPasswordPolicy {
    @Schema(description = "A boolean indicates whether or not the password can contain white spaces", example = "false")
    private boolean whitespacesAllowed;
    @Schema(description = "A boolean indicating whether the same old password can be used for the new one", example = "false")
    private boolean repeatedPasswordAllowed;
    @Schema(description = "Minimum length for a password", example = "6")
    private int minimumLength;
    @Schema(description = "Minimum number of lowercase letters a password must have", example = "1")
    private int minimumLowerCharacters;
    @Schema(description = "Minimum number of uppercase letters a password must have", example = "1")
    private int minimumUpperCharacters;
    @Schema(description = "Minimum number of special letters a password must have", example = "1")
    private int minimumSpecialCharacters;
    @Schema(description = "Maximum number of failed login attempts", example = "3")
    private int maxFailedLoginAttempts;
    private int passwordReuseFrequencyDays;

    @JsonIgnore
    public List<Rule> getPasswordRules () {
        List<Rule> passwordRules = new ArrayList<>();
        if (this.getMinimumLength() > 0) {
            passwordRules.add(new LengthRule(this.getMinimumLength(), Integer.MAX_VALUE));
        }
        if (!this.isWhitespacesAllowed()) {
            passwordRules.add(new WhitespaceRule());
        }
        passwordRules.addAll(getPasswordCharacterRules());
        return passwordRules;
    }

    @JsonIgnore
    public List<CharacterRule> getPasswordCharacterRules() {
        List<CharacterRule> passwordCharacterRules = new ArrayList<>();
        if (this.getMinimumLowerCharacters() > 0) {
            passwordCharacterRules.add(new CharacterRule(EnglishCharacterData.LowerCase, this.getMinimumLowerCharacters()));
        }
        if (this.getMinimumUpperCharacters() > 0) {
            passwordCharacterRules.add(new CharacterRule(EnglishCharacterData.UpperCase, this.getMinimumUpperCharacters()));
        }
        if (this.getMinimumSpecialCharacters() > 0) {
            passwordCharacterRules.add(new CharacterRule(EnglishCharacterData.Special, this.getMinimumSpecialCharacters()));
        }
        return passwordCharacterRules;
    }
}
