package com.oleksandr.monolith.user.mapper;

import com.oleksandr.common.dto.AuthUserDTO;
import com.oleksandr.common.enums.USER_ROLE;
import com.oleksandr.monolith.booking.input.dto.BookingDTO;
import com.oleksandr.monolith.booking.mapper.BookingMapper;
import com.oleksandr.monolith.booking.model.Booking;
import com.oleksandr.monolith.user.model.User;
import com.oleksandr.monolith.user.output.dto.UserFullDTO;
import com.oleksandr.monolith.user.output.dto.UserProfileResponseDTO;
import com.oleksandr.monolith.user.output.dto.UserSummaryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private UserMapper userMapper;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
    }

    @Test
    void mapToDto_shouldMapUserToDTO() {
        // Given
        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());

        User user = new User();
        user.setId(userId);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+1234567890");
        user.setRole(USER_ROLE.USER);
        user.setBookings(List.of(booking));

        BookingDTO bookingDTO = BookingDTO.builder()
                .id(booking.getId())
                .build();

        when(bookingMapper.mapEntityListToDtoList(anyList())).thenReturn(List.of(bookingDTO));

        // When
        UserFullDTO result = userMapper.mapToDto(user);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("johndoe", result.username());
        assertEquals("john@example.com", result.email());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("+1234567890", result.phoneNumber());
        assertEquals(USER_ROLE.USER, result.role());
        assertNotNull(result.bookings());
        assertEquals(1, result.bookings().size());
        verify(bookingMapper, times(1)).mapEntityListToDtoList(anyList());
    }

    @Test
    void mapToDto_shouldHandleNullBookings() {
        // Given
        User user = new User();
        user.setId(userId);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+1234567890");
        user.setRole(USER_ROLE.USER);
        user.setBookings(null);

        // When
        UserFullDTO result = userMapper.mapToDto(user);

        // Then
        assertNotNull(result);
        assertNotNull(result.bookings());
        assertTrue(result.bookings().isEmpty());
    }

    @Test
    void mapToDto_shouldThrowExceptionWhenUserIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userMapper.mapToDto(null));
    }

    @Test
    void mapToEntity_shouldMapDTOToUser() {
        // Given
        UserFullDTO dto = UserFullDTO.builder()
                .id(userId)
                .username("johndoe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .role(USER_ROLE.USER)
                .bookings(List.of())
                .build();

        // When
        User result = userMapper.mapToEntity(dto);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("+1234567890", result.getPhoneNumber());
        assertEquals(USER_ROLE.USER, result.getRole());
        assertNotNull(result.getBookings());
        assertTrue(result.getBookings().isEmpty());
    }

    @Test
    void mapToEntity_shouldThrowExceptionWhenDTOIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userMapper.mapToEntity(null));
    }

    @Test
    void updateUserInformation_shouldUpdateUserFields() {
        // Given
        User user = new User();
        user.setId(userId);
        user.setUsername("oldusername");
        user.setEmail("old@example.com");
        user.setRole(USER_ROLE.USER);

        UserFullDTO dto = UserFullDTO.builder()
                .username("newusername")
                .email("new@example.com")
                .role(USER_ROLE.ADMIN)
                .build();

        // When
        User result = userMapper.updateUserInformation(user, dto);

        // Then
        assertNotNull(result);
        assertEquals("newusername", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(USER_ROLE.ADMIN, result.getRole());
    }

    @Test
    void updateUserInformation_shouldOnlyUpdateNonNullFields() {
        // Given
        User user = new User();
        user.setId(userId);
        user.setUsername("oldusername");
        user.setEmail("old@example.com");
        user.setRole(USER_ROLE.USER);

        UserFullDTO dto = UserFullDTO.builder()
                .username("newusername")
                .email(null)
                .role(null)
                .build();

        // When
        User result = userMapper.updateUserInformation(user, dto);

        // Then
        assertEquals("newusername", result.getUsername());
        assertEquals("old@example.com", result.getEmail());
        assertEquals(USER_ROLE.USER, result.getRole());
    }

    @Test
    void mapListToDtoList_shouldMapListOfUsers() {
        // Given
        User user1 = new User();
        user1.setId(userId);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        user1.setBookings(new ArrayList<>());

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");
        user2.setBookings(new ArrayList<>());

        List<User> users = Arrays.asList(user1, user2);

        when(bookingMapper.mapEntityListToDtoList(anyList())).thenReturn(List.of());

        // When
        List<UserFullDTO> result = userMapper.mapListToDtoList(users);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userId, result.get(0).id());
        assertEquals("user1", result.get(0).username());
    }

    @Test
    void mapListToDtoList_shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<UserFullDTO> result = userMapper.mapListToDtoList(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void mapToSummaryDto_shouldMapUserToSummaryDTO() {
        // Given
        User user = new User();
        user.setId(userId);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+1234567890");

        // When
        UserSummaryDTO result = userMapper.mapToSummaryDto(user);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("johndoe", result.username());
        assertEquals("john@example.com", result.email());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("+1234567890", result.phoneNumber());
    }

    @Test
    void mapToEntityFromAuth_shouldMapAuthDTOToUser() {
        // Given
        AuthUserDTO authDTO = AuthUserDTO.builder()
                .id(userId)
                .username("johndoe")
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .role(USER_ROLE.USER)
                .build();

        // When
        User result = userMapper.mapToEntityFromAuth(authDTO);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("+1234567890", result.getPhoneNumber());
        assertEquals(USER_ROLE.USER, result.getRole());
        assertNotNull(result.getBookings());
        assertTrue(result.getBookings().isEmpty());
    }

    @Test
    void mapToEntityFromAuth_shouldThrowExceptionWhenAuthDTOIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userMapper.mapToEntityFromAuth(null));
    }

    @Test
    void mapToAuthDto_shouldMapUserToAuthDTO() {
        // Given
        User user = new User();
        user.setId(userId);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+1234567890");
        user.setRole(USER_ROLE.USER);

        // When
        AuthUserDTO result = userMapper.mapToAuthDto(user);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("johndoe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("+1234567890", result.getPhoneNumber());
        assertEquals(USER_ROLE.USER, result.getRole());
    }

    @Test
    void mapToAuthDto_shouldThrowExceptionWhenUserIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userMapper.mapToAuthDto(null));
    }

    @Test
    void mapProfileResponseDTO_shouldMapUserToProfileResponseDTO() {
        // Given
        User user = new User();
        user.setId(userId);
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("+1234567890");
        user.setRole(USER_ROLE.USER);

        // When
        UserProfileResponseDTO result = userMapper.mapProfileResponseDTO(user);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("johndoe", result.username());
        assertEquals("john@example.com", result.email());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals("+1234567890", result.phoneNumber());
        assertEquals(USER_ROLE.USER, result.role());
    }

    @Test
    void mapProfileResponseDTO_shouldThrowExceptionWhenUserIsNull() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userMapper.mapProfileResponseDTO(null));
    }
}
