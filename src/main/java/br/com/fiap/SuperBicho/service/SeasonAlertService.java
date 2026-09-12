package br.com.fiap.SuperBicho.service;

import br.com.fiap.SuperBicho.dto.response.SeasonAlertResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

@Service
public class SeasonAlertService {

    public SeasonAlertResponseDTO buildSeasonAlert() {
        Month mes = LocalDate.now().getMonth();

        return switch (mes) {
            case DECEMBER, JANUARY, FEBRUARY -> new SeasonAlertResponseDTO(
                    "Calor", "Estamos no verão: fique de olho em quanto o " +
                    "seu pet está bebendo água...",
                    "https://petcare.com.br/como-medir-a-quantidade-de-agua-que-seu-cao-ou-gato-adulto-bebe/",
                    "Saiba mais sobre hidratação no calor");

            case JUNE, JULY, AUGUST -> new SeasonAlertResponseDTO(
                    "Frio", "Estamos no inverno: atenção à queda de temperatura...",
                    "https://purina.com.br/purina/cachorro-sente-frio-9-cuidados-inverno",
                    "Saiba mais sobre cuidados no frio");

            case MARCH, APRIL, MAY -> new SeasonAlertResponseDTO(
                    "Outono", "Estamos no outono: fique atento a mudanças bruscas" +
                    " de temperatura entre dia e noite.",
                    "https://purina.com.br/purina/cachorro-sente-frio-9-cuidados-inverno",
                    "Saiba mais sobre cuidados no outono");

            default -> new SeasonAlertResponseDTO("Primavera",
                    "Estamos na primavera: época de queda de pelo e maior incidência " +
                            "de pulgas/carrapatos, redobre a atenção.",
                    "https://petcare.com.br/parasitas-em-animais-pulgas-carrapatos-mosquitos-identificar-e-prevenir/",
                    "Saiba mais sobre cuidados na primavera");
        };
    }
}