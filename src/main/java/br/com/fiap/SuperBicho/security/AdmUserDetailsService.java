package br.com.fiap.SuperBicho.security;

import br.com.fiap.SuperBicho.entity.Adm;
import br.com.fiap.SuperBicho.repository.AdmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdmUserDetailsService implements UserDetailsService {
    private final AdmRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Adm adm = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin não encontrado"));

        return org.springframework.security.core.userdetails.User
                .withUsername(adm.getEmail())
                .password(adm.getPassword())
                .authorities("ROLE_ADMIN")
                .build();
    }
}