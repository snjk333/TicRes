package com.oleksandr.monolith.User.Service;

import com.oleksandr.monolith.User.DTO.*;
import com.oleksandr.monolith.User.EntityRepo.User;
import com.oleksandr.monolith.User.EntityRepo.UserRepository;
import com.oleksandr.monolith.User.util.UserMapper;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import com.oleksandr.monolith.integration.auth.AuthClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthClientService authClientService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, AuthClientService authClientService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authClientService = authClientService;
    }

    /**
     * Находит пользователя в локальной БД или создает его,
     * получая данные из внешнего сервиса.
     * Содержит надежную обработку гонки потоков (race condition).
     */
    @Transactional
    @Override
    public User getOrCreateUser(UUID userId) {
        log.info("Looking for user with ID: {}", userId);
        return userRepository.findById(userId).orElseGet(() -> {
            log.info("User not found locally, fetching from Auth service: {}", userId);
            AuthUserDTO userDTO = authClientService.getUserById(userId);
            User user = userMapper.mapToEntityFromAuth(userDTO);
            try {
                User savedUser = userRepository.saveAndFlush(user);
                log.info("User created locally with ID: {}", savedUser.getId());
                return savedUser;
            } catch (DataIntegrityViolationException e) {
                log.warn("Race condition detected while creating user ID: {}. Fetching existing user.", userId);
                return userRepository.findById(userId)
                        .orElseThrow(() -> {
                            log.error("User not found after race condition for ID: {}", userId);
                            return new ResourceNotFoundException("User not found after race condition for ID: " + userId);
                        });
            }
        });
    }

    @Transactional
    @Override
    public UserProfileResponseDTO getUserProfile(UUID id) {
        User user = getOrCreateUser(id);
        return userMapper.mapProfileResponseDTO(user);
    }

    @Override
    public UserSummaryDTO updateUserProfile(UUID id, UserUpdateRequestDTO request) {
        User user = getOrCreateUser(id);
        updateUserFields(user, request);
        authClientService.updateUser(userMapper.mapToAuthDto(user));
        return userMapper.mapToSummaryDto(user);
    }

    private void updateUserFields(User user, UserUpdateRequestDTO request) {
        if(request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
    }

}