package br.com.fiap.SuperBicho.security;

import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.repository.ClinicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicUserDetailsService implements UserDetailsService {
    private final ClinicRepository clinicRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Clinic clinic = clinicRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Clínica não encontrada"));
        String authority = "ROLE_CLINICA_" + clinic.getClinicStatus().name();

        return org.springframework.security.core.userdetails.User
                .withUsername(clinic.getEmail())
                .password(clinic.getPassword())
                .authorities(authority)
                .build();
    }
}