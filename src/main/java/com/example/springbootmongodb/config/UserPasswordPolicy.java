package com.example.springbootmongodb.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private boolean whitespacesAllowed;
    private boolean repeatedPasswordAllowed;
    private int minimumLength;
    private int minimumLowerCharacters;
    private int minimumUpperCharacters;
    private int minimumSpecialCharacters;
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
