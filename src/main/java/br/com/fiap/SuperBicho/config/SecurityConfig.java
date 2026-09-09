package br.com.fiap.SuperBicho.config;

import br.com.fiap.SuperBicho.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final GuardianUserDetailsService guardianUserDetailsService;
    private final ClinicUserDetailsService clinicUserDetailsService;
    private final AdmUserDetailsService adminUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private DaoAuthenticationProvider providerFor(org.springframework.security.core.userdetails.UserDetailsService uds) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/adm/**")
                .authenticationProvider(providerFor(adminUserDetailsService))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/adm/login").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .formLogin(form -> form
                        .loginPage("/adm/login")
                        .loginProcessingUrl("/adm/login")
                        .defaultSuccessUrl("/adm/home", true)
                        .permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain clinicFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/clinica/**")
                .authenticationProvider(providerFor(clinicUserDetailsService))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/clinica/login", "/clinica/cadastro").permitAll()
                        .requestMatchers("/clinica/status").hasAnyRole("CLINICA_PENDING", "CLINICA_APPROVED", "CLINICA_DENIED")
                        .anyRequest().hasRole("CLINICA_APPROVED"))
                .formLogin(form -> form
                        .loginPage("/clinica/login")
                        .loginProcessingUrl("/clinica/login") //
                        .defaultSuccessUrl("/clinica/status", true)
                        .permitAll());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain tutorFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/**")
                .authenticationProvider(providerFor(guardianUserDetailsService))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/cadastro", "/css/**", "/js/**", "/images/**").permitAll()
                        .anyRequest().hasRole("TUTOR"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/guardian/home", true)
                        .permitAll());
        return http.build();
    }
}