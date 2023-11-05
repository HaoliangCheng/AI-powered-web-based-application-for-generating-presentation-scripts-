package com.example.uploadingfiles.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;

import com.example.uploadingfiles.LogoutHandler;
import com.example.uploadingfiles.service.MyUserDetailService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Autowired
	private MyUserDetailService myUserDetailService;
	private LogoutHandler logoutSuccessHandler;
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(myUserDetailService);
	}

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
            .requestMatchers("/","/register","/verify", "/verificationSuccess").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login").permitAll()
            .defaultSuccessUrl("/authorizedPage", true)
            .failureUrl("/login?error=true")
            .and()
            .logout().logoutSuccessUrl("/").permitAll();
        return http.build();
    }
    
    
}