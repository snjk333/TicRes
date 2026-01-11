package com.oleksandr.monolith.user.mapper;

import com.oleksandr.common.dto.AuthUserDTO;
import com.oleksandr.monolith.booking.input.dto.BookingDTO;
import com.oleksandr.monolith.booking.mapper.BookingMapper;
import com.oleksandr.monolith.user.model.User;
import com.oleksandr.monolith.user.output.dto.UserFullDTO;
import com.oleksandr.monolith.user.output.dto.UserProfileResponseDTO;
import com.oleksandr.monolith.user.output.dto.UserSummaryDTO;
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
    public UserFullDTO mapToDto(User user) {
        if (user == null) throw new IllegalArgumentException("User entity cannot be null");

        List<BookingDTO> bookingsDto = user.getBookings() != null
                ? bookingMapper.mapEntityListToDtoList(user.getBookings())
                : List.of();

        return UserFullDTO.builder()
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
    public User mapToEntity(UserFullDTO dto) {
        if (dto == null) throw new IllegalArgumentException("UserDTO cannot be null");

        User user = new User();
        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setPhoneNumber(dto.phoneNumber());
        user.setRole(dto.role());
        user.setBookings(new ArrayList<>()); // booking add at service from db
        return user;
    }

    public User updateUserInformation(User user, UserFullDTO dto) {
        if (dto.username() != null) user.setUsername(dto.username());
        if (dto.email() != null) user.setEmail(dto.email());
        if (dto.role() != null) user.setRole(dto.role());
        return user;
    }

    public List<UserFullDTO> mapListToDtoList(List<User> users) {
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