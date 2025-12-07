package com.oleksandr.monolith.User.Service;

import com.oleksandr.monolith.User.DTO.UserDTO;
import com.oleksandr.monolith.User.DTO.UserProfileResponseDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.DTO.UserUpdateRequestDTO;
import com.oleksandr.monolith.User.EntityRepo.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface UserService {

    @Transactional
    User getOrCreateUser(UUID userId);

    UserProfileResponseDTO getUserProfile(UUID id);

    UserSummaryDTO updateUserProfile(UUID id, UserUpdateRequestDTO request);
}
