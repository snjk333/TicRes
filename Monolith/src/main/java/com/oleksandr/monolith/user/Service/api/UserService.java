package com.oleksandr.monolith.user.Service.api;

import com.oleksandr.monolith.user.output.dto.UserProfileResponseDTO;
import com.oleksandr.monolith.user.output.dto.UserSummaryDTO;
import com.oleksandr.monolith.user.input.dto.UserUpdateRequestDTO;
import com.oleksandr.monolith.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {

    @Transactional
    User getOrCreateUser(UUID userId);

    UserProfileResponseDTO getUserProfile(UUID id);

    UserSummaryDTO updateUserProfile(UUID id, UserUpdateRequestDTO request);
}
