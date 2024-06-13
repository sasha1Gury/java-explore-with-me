package ru.practicum.ewm.service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {
    private Set<Long> events;

    private Boolean pinned;

    @NotEmpty
    private String title;
}