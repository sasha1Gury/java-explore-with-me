package ru.practicum.ewm.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.service.UserService;
import ru.practicum.ewm.service.user.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Slf4j
@Validated
public class UserController {
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(@RequestParam(name = "ids", required = false) List<Long> userIdList,
                                     @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                     @Positive @RequestParam(defaultValue = "10") int size) {

        log.info("Admin get all users: ids = {}, from = {}, size = {}", userIdList, from, size);
        return userService.getAllUsers(userIdList, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.info("Admin create user: {}", userDto);
        User user = mapper.map(userDto, User.class);
        return mapper.map(userService.saveUser(user), UserDto.class);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable long userId) {
        log.info("Admin delete user: userId = {}", userId);
        userService.delete(userId);
    }
}
