package br.com.fiap.SuperBicho.dto.response;

import lombok.*;

@Getter @Setter @AllArgsConstructor
public class VaccineCalendarItemDTO {
    private Integer vaccineId;
    private String vaccineName;
    private String description;
    private String lastApplicationDate;
    private String nextDueDate;
    private String calendarStatus;
    private boolean appliedToday;
}