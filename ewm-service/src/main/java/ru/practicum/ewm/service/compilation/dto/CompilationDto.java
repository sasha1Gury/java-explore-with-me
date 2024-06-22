package ru.practicum.ewm.service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.service.event.dto.EventShortDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    @PositiveOrZero
    private long id;

    private List<EventShortDto> events;

    @NotNull
    private Boolean pinned;

    @NotEmpty
    @Size(max = 50, message = "Поле title не должно превышать по длине 50 символов")
    private String title;
}
