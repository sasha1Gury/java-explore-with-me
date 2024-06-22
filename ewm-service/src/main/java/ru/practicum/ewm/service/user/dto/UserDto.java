package ru.practicum.ewm.service.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;

    @NotEmpty
    @NotBlank
    @Size(min = 2, max = 250, message = "Длина имени должна быть от 2 до 250 симвовлов")
    private String name;

    @Email
    @NotBlank
    @Size(min = 6, max = 254, message = "Длина почты должна быть от 6 до 254 симвовлов")
    private String email;
}
