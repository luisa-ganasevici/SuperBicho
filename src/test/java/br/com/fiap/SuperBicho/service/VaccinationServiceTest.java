package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.response.VaccineCalendarItemDTO;
import br.com.fiap.SuperBicho.entity.Animal;
import br.com.fiap.SuperBicho.entity.AnimalVaccination;
import br.com.fiap.SuperBicho.entity.Vaccine;
import br.com.fiap.SuperBicho.repository.AnimalRepository;
import br.com.fiap.SuperBicho.repository.AnimalVaccinationRepository;
import br.com.fiap.SuperBicho.repository.VaccineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VaccinationServiceTest {

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private AnimalVaccinationRepository animalVaccinationRepository;

    @Mock
    private AnimalRepository animalRepository;

    @InjectMocks
    private VaccinationService vaccinationService;

    private Vaccine vacinaComReforco;
    private Vaccine vacinaDoseUnica;
    private Animal animal;

    @BeforeEach
    public void arrange() {
        animal = new Animal();
        animal.setId(1);
        animal.setName("Rex");

        vacinaComReforco = new Vaccine(1, "V10", "Cachorro", "Polivalente canina", 2, 12);
        vacinaDoseUnica = new Vaccine(2, "Raiva", "Cachorro", "Antirrábica", 3, null);

        when(vaccineRepository.findAllByOrderByNameAsc()).thenReturn(List.of(vacinaComReforco));
    }

    @Test
    public void deveRotularComoNuncaAplicadaQuandoNaoHaAplicacao() {
        // arrange
        when(animalVaccinationRepository.findByAnimalId(1)).thenReturn(List.of());

        // act
        List<VaccineCalendarItemDTO> calendario = vaccinationService.buildCalendar(1);

        // assert
        assertEquals(1, calendario.size());
        assertEquals("NUNCA_APLICADA", calendario.get(0).getCalendarStatus());
        assertNull(calendario.get(0).getLastApplicationDate());
    }

    @Test
    public void deveRotularComoDoseUnicaConcluidaQuandoVacinaNaoTemReforco() {
        // arrange
        when(vaccineRepository.findAllByOrderByNameAsc()).thenReturn(List.of(vacinaDoseUnica));
        AnimalVaccination aplicacao = new AnimalVaccination(10, "2026-01-10", animal, vacinaDoseUnica);
        when(animalVaccinationRepository.findByAnimalId(1)).thenReturn(List.of(aplicacao));

        // act
        List<VaccineCalendarItemDTO> calendario = vaccinationService.buildCalendar(1);

        // assert
        assertEquals("DOSE_UNICA_CONCLUIDA", calendario.get(0).getCalendarStatus());
        assertEquals("2026-01-10", calendario.get(0).getLastApplicationDate());
        assertNull(calendario.get(0).getNextDueDate());
    }

    @Test
    public void deveRotularComoAtrasadaQuandoProximaDoseJaPassou() {
        // arrange: aplicada há 13 meses, reforço é a cada 12 meses -> venceu
        String dataAplicacao = LocalDate.now().minusMonths(13).format(ISO);
        AnimalVaccination aplicacao = new AnimalVaccination(11, dataAplicacao, animal, vacinaComReforco);
        when(animalVaccinationRepository.findByAnimalId(1)).thenReturn(List.of(aplicacao));

        // act
        List<VaccineCalendarItemDTO> calendario = vaccinationService.buildCalendar(1);

        // assert
        assertEquals("ATRASADA", calendario.get(0).getCalendarStatus());
    }

    @Test
    public void deveRotularComoProximaQuandoFaltamMenosDe30Dias() {
        // arrange: aplicada há 11 meses e 5 dias, reforço a cada 12 meses -> vence em ~25 dias
        String dataAplicacao = LocalDate.now().minusMonths(11).minusDays(5).format(ISO);
        AnimalVaccination aplicacao = new AnimalVaccination(12, dataAplicacao, animal, vacinaComReforco);
        when(animalVaccinationRepository.findByAnimalId(1)).thenReturn(List.of(aplicacao));

        // act
        List<VaccineCalendarItemDTO> calendario = vaccinationService.buildCalendar(1);

        // assert
        assertEquals("PROXIMA", calendario.get(0).getCalendarStatus());
    }

    @Test
    public void deveRotularComoEmDiaQuandoFaltamMaisDe30Dias() {
        // arrange: aplicada há 2 meses, reforço a cada 12 meses -> vence em 10 meses
        String dataAplicacao = LocalDate.now().minusMonths(2).format(ISO);
        AnimalVaccination aplicacao = new AnimalVaccination(13, dataAplicacao, animal, vacinaComReforco);
        when(animalVaccinationRepository.findByAnimalId(1)).thenReturn(List.of(aplicacao));

        // act
        List<VaccineCalendarItemDTO> calendario = vaccinationService.buildCalendar(1);

        // assert
        assertEquals("EM_DIA", calendario.get(0).getCalendarStatus());
    }

    @Test
    public void deveConsiderarSomenteAAplicacaoMaisRecenteQuandoHaVariasDoses() {
        // arrange: duas aplicações da mesma vacina, deve considerar a mais recente
        String dataAntiga = LocalDate.now().minusMonths(20).format(ISO);
        String dataRecente = LocalDate.now().minusMonths(1).format(ISO);
        AnimalVaccination aplicacaoAntiga = new AnimalVaccination(14, dataAntiga, animal, vacinaComReforco);
        AnimalVaccination aplicacaoRecente = new AnimalVaccination(15, dataRecente, animal, vacinaComReforco);
        when(animalVaccinationRepository.findByAnimalId(1)).thenReturn(List.of(aplicacaoAntiga, aplicacaoRecente));

        // act
        List<VaccineCalendarItemDTO> calendario = vaccinationService.buildCalendar(1);

        // assert
        assertEquals(dataRecente, calendario.get(0).getLastApplicationDate());
        assertEquals("EM_DIA", calendario.get(0).getCalendarStatus());
    }
}