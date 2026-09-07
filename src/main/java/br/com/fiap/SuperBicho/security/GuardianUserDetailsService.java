package br.com.fiap.SuperBicho.security;

import br.com.fiap.SuperBicho.entity.Guardian;
import br.com.fiap.SuperBicho.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuardianUserDetailsService implements UserDetailsService {
    private final GuardianRepository guardianRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Guardian guardian = guardianRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Tutor não encontrado"));

        return org.springframework.security.core.userdetails.User
                .withUsername(guardian.getEmail())
                .password(guardian.getPassword())
                .authorities("ROLE_TUTOR")
                .build();
    }
}