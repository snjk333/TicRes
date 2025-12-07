package com.oleksandr.monolith.User.util;

import com.oleksandr.monolith.Booking.DTO.BookingDTO;
import com.oleksandr.monolith.Booking.util.BookingMapper;
import com.oleksandr.common.dto.AuthUserDTO;
import com.oleksandr.monolith.User.DTO.UserDTO;
import com.oleksandr.monolith.User.DTO.UserProfileResponseDTO;
import com.oleksandr.monolith.User.DTO.UserSummaryDTO;
import com.oleksandr.monolith.User.EntityRepo.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class UserMapper {

    private final BookingMapper bookingMapper;

    public UserMapper(BookingMapper bookingMapper) {
        this.bookingMapper = bookingMapper;
    }

    // Entity → DTO
    public UserDTO mapToDto(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        List<BookingDTO> bookingsDto = user.getBookings() != null
                ? bookingMapper.mapEntityListToDtoList(user.getBookings())
                : List.of();

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .bookings(bookingsDto)
                .build();
    }

    // DTO → Entity
    public User mapToEntity(UserDTO dto) {
        if (dto == null) throw new IllegalArgumentException("UserDTO cannot be null");

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        user.setBookings(new ArrayList<>()); // booking add at service from db
        return user;
    }

    public User updateUserInformation(User user, UserDTO dto) {
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        return user;
    }

    public List<UserDTO> mapListToDtoList(List<User> users) {
        return users == null ? List.of() :
                users.stream()
                        .map(this::mapToDto)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public UserSummaryDTO mapToSummaryDto(User user) {
        return UserSummaryDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public User mapToEntityFromAuth(AuthUserDTO dto) {
        if (dto == null) throw new IllegalArgumentException("AuthUserDTO cannot be null");

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBookings(new ArrayList<>());
        return user;
    }

    public AuthUserDTO mapToAuthDto(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        return AuthUserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }

    public UserProfileResponseDTO mapProfileResponseDTO(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        return UserProfileResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }
}