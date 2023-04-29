package com.eshare.shiro.configuration;

import org.apache.shiro.realm.Realm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public Realm customRealm() {
        return new CustomRealm();
    }
}
