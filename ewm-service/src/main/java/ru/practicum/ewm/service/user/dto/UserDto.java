package ru.practicum.ewm.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;
}
