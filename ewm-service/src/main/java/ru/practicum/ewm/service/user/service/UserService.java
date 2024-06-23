package ru.practicum.ewm.service.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.exceptions.UserEmailNotUniqueException;
import ru.practicum.ewm.service.exceptions.UserNotFoundException;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public User saveUser(User user) {
        User user1;
        try {
            user1 = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new UserEmailNotUniqueException("e-mail не уникален");
        }
        return user1;
    }

    public List<UserDto> getAllUsers(List<Long> userIdList, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);

        if (userIdList == null || userIdList.isEmpty()) {
            return userRepository.findAll(page).getContent().stream()
                    .map(user ->  mapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
        } else {
            return userRepository.findByIdInOrderById(userIdList, page)
                    .stream()
                    .map(user ->  mapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
        }
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь " + userId + " не наден"));
    }

    public void delete(Long userId) {
        userRepository.delete(getUserById(userId));
    }
}
