package ru.practicum.ewm.service.user.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import ru.practicum.ewm.service.user.dto.UserDto;
import ru.practicum.ewm.service.user.model.User;
import ru.practicum.ewm.service.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper = new ModelMapper();

    public User saveUser(User user) {
        User user1;
        try {
            user1 = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("e-mail не уникален");
        }
        return user1;
    }

    public List<UserDto> getAllUsers(List<Long> userIdList, int from, int size) {
        Pageable page = PageRequest.of(from/size, size);

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
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Пользователь " + userId + " не найден");
        } else {
            return optionalUser.get();
        }
    }

    public void delete(Long userId) {
        userRepository.delete(getUserById(userId));
    }
}
