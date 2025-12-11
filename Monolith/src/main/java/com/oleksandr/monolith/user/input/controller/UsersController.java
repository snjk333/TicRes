package com.oleksandr.monolith.user.input.controller;

import com.oleksandr.monolith.booking.input.dto.BookingSummaryDTO;
import com.oleksandr.monolith.coordinator.BookingCoordinator;
import com.oleksandr.monolith.user.output.dto.UserProfileResponseDTO;
import com.oleksandr.monolith.user.output.dto.UserSummaryDTO;
import com.oleksandr.monolith.user.input.dto.UserUpdateRequestDTO;
import com.oleksandr.monolith.user.Service.api.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UsersController {

    private final UserService userService;
    private final BookingCoordinator bookingCoordinator;

    public UsersController(UserService userService, BookingCoordinator bookingCoordinator) {
        this.userService = userService;
        this.bookingCoordinator = bookingCoordinator;
    }

    @GetMapping("/{id}")
    public UserProfileResponseDTO getUserProfile(@PathVariable UUID id) {
        return userService.getUserProfile(id);
    }

    @PatchMapping("/{id}")
    public UserSummaryDTO updateUserProfile(
            @PathVariable UUID id,
            @RequestBody UserUpdateRequestDTO request
    ) {
        return userService.updateUserProfile(id, request);
    }


    @GetMapping("/{id}/bookings")
    public List<BookingSummaryDTO> getUserBookings(@PathVariable UUID id) {
        return bookingCoordinator.getUserBookings(id);
    }




}

