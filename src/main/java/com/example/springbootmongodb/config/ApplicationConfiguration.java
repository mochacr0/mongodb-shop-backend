package com.example.springbootmongodb.config;

import com.example.springbootmongodb.common.validator.CommonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMongoAuditing
public class ApplicationConfiguration {
    @Bean
    public PasswordEncoder buildPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommonValidator buildCommonDataValidator(@Autowired SecuritySettingsConfiguration securitySettings) {
        return new CommonValidator(securitySettings);
    }

    @Bean
    public MongoTransactionManager buildMongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }

    @Autowired
    public void setMapKeyDotReplacement(MappingMongoConverter converter) {
        converter.setMapKeyDotReplacement("~");
    }


}
