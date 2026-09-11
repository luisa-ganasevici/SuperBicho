package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.request.VeterinarianRequestDTO;
import br.com.fiap.SuperBicho.dto.response.VeterinarianResponseDTO;
import br.com.fiap.SuperBicho.entity.Clinic;
import br.com.fiap.SuperBicho.entity.Veterinarian;
import br.com.fiap.SuperBicho.repository.ClinicRepository;
import br.com.fiap.SuperBicho.repository.VeterinarianRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class VeterinarianService {

    private final VeterinarianRepository veterinarianRepository;
    private final ClinicRepository clinicRepository;
    private final ClinicService clinicService;
    private final AppointmentService appointmentService;
    private final CheckUpService checkUpService;

    private static final String MOTIVO_CANCELAMENTO_VET =
            "Consulta cancelada, favor entrar em contato com a SuperBicho para retirar as dúvidas.";

    public VeterinarianResponseDTO create(VeterinarianRequestDTO dto) {
        Veterinarian veterinarian = new Veterinarian();
        veterinarian.setName(dto.getName());
        veterinarian.setSpecialty(dto.getSpecialty());
        veterinarian.setClinic(resolveClinic(dto.getClinicId()));
        return toResponse(veterinarianRepository.save(veterinarian));
    }

    public List<VeterinarianResponseDTO> findByClinic(Integer clinicId) {
        return veterinarianRepository.findByClinicId(clinicId).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<VeterinarianResponseDTO> findAll() {
        return veterinarianRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteByIdForClinic(Integer id, Integer clinicId) {
        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> notFound("Veterinarian"));
        if (!veterinarian.getClinic().getId().equals(clinicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Veterinarian does not belong to this clinic");
        }
        veterinarianRepository.deleteById(id);
    }

    public void deleteByAdmin(Integer id) {
        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> notFound("Veterinarian"));
        String nome = veterinarian.getName();
        Integer clinicId = veterinarian.getClinic().getId();

        appointmentService.cancelAllByVeterinarian(id, MOTIVO_CANCELAMENTO_VET);
        checkUpService.cancelAllByVeterinarian(id, MOTIVO_CANCELAMENTO_VET);
        clinicService.setNotice(clinicId,
                "Seu veterinário " + nome + " foi excluído. Favor entrar em contato com a SuperBicho.");

        veterinarianRepository.deleteById(id);
    }

    private Clinic resolveClinic(Integer clinicId) {
        return clinicRepository.findById(clinicId).orElseThrow(() -> notFound("Clinic"));
    }

    private VeterinarianResponseDTO toResponse(Veterinarian veterinarian) {
        return new VeterinarianResponseDTO(veterinarian.getId(), veterinarian.getName(),
                veterinarian.getSpecialty(), veterinarian.getClinic().getId(), veterinarian.getClinic().getName());
    }

    private ResponseStatusException notFound(String resource) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, resource + " not found");
    }
}