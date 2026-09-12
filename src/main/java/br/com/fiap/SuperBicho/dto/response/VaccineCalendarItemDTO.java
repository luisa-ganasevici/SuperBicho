package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class VaccineCalendarItemDTO {
    private Integer vaccineId;
    private String vaccineName;
    private String description;
    private String lastApplicationDate;   // null se nunca foi aplicada
    private String nextDueDate;           // null se dose única já concluída
    private String calendarStatus;        // NUNCA_APLICADA, EM_DIA, PROXIMA, ATRASADA, DOSE_UNICA_CONCLUIDA
}